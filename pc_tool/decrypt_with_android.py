import os
import sys
import time
import subprocess
import shutil

# Configuration
ADB_PATH = "adb"  # Ensure adb is in PATH or provide full path
TEMP_REMOTE_DIR = "/sdcard/UnDRM"
REMOTE_INPUT_FILE = f"{TEMP_REMOTE_DIR}/temp.drm"
REMOTE_SIGNAL_DONE = "/sdcard/undrm_done.txt"
REMOTE_SIGNAL_FAIL = "/sdcard/undrm_fail.txt"
APP_PACKAGE = "com.wjthinkbig.undrmapp"
ACTIVITY_NAME = ".MainActivity"

def run_adb(command):
    full_cmd = f"{ADB_PATH} {command}"
    print(f"Running: {full_cmd}")
    result = subprocess.run(full_cmd, shell=True, capture_output=True, text=True)
    if result.returncode != 0:
        print(f"ADB Error: {result.stderr}")
    return result

def main():
    if len(sys.argv) < 2:
        print("Usage: python decrypt_with_android.py <input_drm_file> [output_dir]")
        sys.exit(1)

    input_file = sys.argv[1]
    output_dir = sys.argv[2] if len(sys.argv) > 2 else os.getcwd()

    if not os.path.exists(input_file):
        print(f"Error: Input file not found: {input_file}")
        sys.exit(1)

    print("=== Starting DRM Decryption (Android Hybrid) ===")

    # 1. Setup Remote Environment
    print("[1/5] Cleaning up remote temp files...")
    run_adb(f"shell rm -rf {TEMP_REMOTE_DIR}")
    run_adb(f"shell mkdir -p {TEMP_REMOTE_DIR}")
    run_adb(f"shell rm {REMOTE_SIGNAL_DONE}")
    run_adb(f"shell rm {REMOTE_SIGNAL_FAIL}")

    # 2. Push File
    print(f"[2/5] Pushing file to device: {REMOTE_INPUT_FILE}")
    push_res = run_adb(f"push \"{input_file}\" \"{REMOTE_INPUT_FILE}\"")
    if push_res.returncode != 0:
        print("Failed to push file.")
        sys.exit(1)

    # 3. Run App
    print("[3/5] Launching Android App for decryption...")
    # Grant permissions if needed (storage) - Android 10+ might need scoped storage handling, but assuming basic permission granting for now
    run_adb(f"shell pm grant {APP_PACKAGE} android.permission.WRITE_EXTERNAL_STORAGE")
    run_adb(f"shell pm grant {APP_PACKAGE} android.permission.READ_EXTERNAL_STORAGE")
    
    # Start Activity with Intent
    # Force stop first to ensure fresh state
    run_adb(f"shell am force-stop {APP_PACKAGE}")
    run_adb(f"shell am start -n {APP_PACKAGE}/{ACTIVITY_NAME} --es target_path \"{REMOTE_INPUT_FILE}\"")

    # 4. Wait for Completion
    print("[4/5] Waiting for decryption to complete...")
    max_retries = 30 # 30 seconds timeout
    success = False
    
    for i in range(max_retries):
        # Check success signal
        res_done = run_adb(f"shell ls {REMOTE_SIGNAL_DONE}")
        if REMOTE_SIGNAL_DONE in res_done.stdout:
            success = True
            break
        
        # Check fail signal
        res_fail = run_adb(f"shell ls {REMOTE_SIGNAL_FAIL}")
        if REMOTE_SIGNAL_FAIL in res_fail.stdout:
            print("Error: App reported decryption failure.")
            break
            
        time.sleep(1)
        print(".", end="", flush=True)
    
    print("") # Newline

    if success:
        print("[5/5] Decryption SUCCESS! Pulling file...")
        # The output file name depends on what the jar does.
        # Usually it appends or modifies the extension.
        # We'll list files in the temp dir and find the one that isn't the input.
        
        # Note: Android 11+ might store it in app-specific dir if scoped storage is enforced, 
        # but code suggests it returns a path (DrmDecode task returns sFullPath).
        # We assume the jar writes to the same directory or a predictable one.
        # Let's check the temp dir.
        ls_res = run_adb(f"shell ls {TEMP_REMOTE_DIR}")
        files = ls_res.stdout.strip().split()
        
        output_remote_path = None
        for f in files:
            if f != os.path.basename(REMOTE_INPUT_FILE) and (f.endswith(".mp4") or f.endswith(".mp3") or "dec" in f):
                output_remote_path = f"{TEMP_REMOTE_DIR}/{f}"
                break
        
        if output_remote_path:
            local_name = os.path.basename(output_remote_path)
            local_full_path = os.path.join(output_dir, local_name)
            run_adb(f"pull \"{output_remote_path}\" \"{local_full_path}\"")
            print(f"File saved to: {local_full_path}")
        else:
            print("Warning: Could not identify output file in remote temp directory.")
            print("Remote files:", files)

    else:
        print("Timeout or Failure. Please check device logs.")
        logs = run_adb(f"logcat -d -s UnDRM:D *:E | tail -n 20")
        print(logs.stdout)

    # Cleanup
    # run_adb(f"shell rm -rf {TEMP_REMOTE_DIR}")

if __name__ == "__main__":
    main()
