package com.wjthinkbig.undrmapp;

import android.graphics.Bitmap;
import android.net.Uri;

public class MediaWrapper {
    public final static String TAG = "VLC/MediaWrapper";
    public final static int TYPE_ALL = -1;
    public final static int TYPE_VIDEO = 0;
    public final static int TYPE_AUDIO = 1;
    public final static int TYPE_GROUP = 2;
    public final static int TYPE_DIR = 3;
    public final static int TYPE_SUBTITLE = 4;
    public final static int TYPE_PLAYLIST = 5;
    public final static int TYPE_STREAM = 6;

    public final static int MEDIA_VIDEO = 0x01;
    public final static int MEDIA_NO_HWACCEL = 0x02;
    public final static int MEDIA_PAUSED = 0x4;
    public final static int MEDIA_FORCE_AUDIO = 0x8;

    //MetaData flags
    public final static int META_RATING = 1;
    //Playback
    public final static int META_PROGRESS = 50;
    public final static int META_SPEED = 51;
    public final static int META_TITLE = 52;
    public final static int META_CHAPTER = 53;
    public final static int META_PROGRAM = 54;
    public final static int META_SEEN = 55;
    //video
    public final static int META_VIDEOTRACK = 100;
    public final static int META_ASPECT_RATIO = 101;
    public final static int META_ZOOM = 102;
    public final static int META_CROP = 103;
    public final static int META_DEINTERLACE = 104;
    public final static int META_VIDEOFILTER = 105;
    //Audio
    public final static int META_AUDIOTRACK = 150;
    public final static int META_GAIN = 151;
    public final static int META_AUDIODELAY = 152;
    //Spu
    public final static int META_SUBTITLE_TRACK = 200;
    public final static int META_SUBTITLE_DELAY = 201;
    //Various
    public final static int META_APPLICATION_SPECIFIC = 250;

    // threshold lentgh between song and podcast ep, set to 15 minutes
    private static final long PODCAST_THRESHOLD = 900000L;

    protected String mDisplayTitle;
    private String mArtist;
    private String mGenre;
    private String mCopyright;
    private String mAlbum;
    private int mTrackNumber;
    private int mDiscNumber;
    private String mAlbumArtist;
    private String mRating;
    private String mDate;
    private String mSettings;
    private String mNowPlaying;
    private String mPublisher;
    private String mEncodedBy;
    private String mTrackID;
    private String mArtworkURL;

    private Uri mUri;
    private String mFilename;
    private long mTime = 0;
    /* -1 is a valid track (Disabled) */
    private int mAudioTrack = -2;
    private int mSpuTrack = -2;
    private long mLength = 0;
    private int mType;
    private int mWidth = 0;
    private int mHeight = 0;
    private Bitmap mPicture;
    private boolean mIsPictureParsed;
    private int mFlags = 0;
    private long mLastModified = 0l;

    private long mSeen = 0l;

    private Uri mUriDRM = null;
    private boolean mIsDRMDone = false;
    private String memberCode;
    private String title;
    private String albumTitle;
    private String bookCode;
    private String contentsCode;
    private String exhibitionType;
    private String exhibitionCode;
    private String orderID;
    private String mediaTP;
    private String mediaFM;
    private String serviceTP;
    private String compose;
    private String relationGoodsCode;
    private String thumbnailURL;
    private String thumbnailModified;
    private String lyrics;
    private String expireDate;
    private long startPosition;         // 서버에서 받은 시간? (이전에 봤던 동영상일 경우, 이어보기 기능?)
    private boolean isCheck = false;
    private boolean isFirstPlay = true;
    private String drmYN = "Y";
    private String mAfterReadingDoFlag = "N";

    //디폴트는 반드시 true!!!!
    private boolean isFileExists = true;

    //2018.01.09 kimdo 추가
    private int mRequestCode = -11;

    //2020.11.13 ytkim 김영태 : 영상 2개 이상일경우, 영상을 다 봤으면 다음 영상을 자동 재생해야 한다.
    private boolean isComplete =  false; //영상을 다 봤다(true). 안 봤다.(false)  : 완료 판단 기준은 98% 이상?
    private long currentPosition = 0l;  // 현재까지 본 영상 위치
    private String buttonName = "";     // 플레이어. 하단에 다음 영상보기 버튼이 있다. (ex:선생님 영상보기) 여기에 들어갈 문구.



    public boolean isDRMDone() {
        return mIsDRMDone;
    }

    public void setDRMDone(boolean mIsDRMDone) {
        this.mIsDRMDone = mIsDRMDone;
    }

    public void setUriDRM(Uri uriDRM){
//        Logger.d(TAG, "setUriDRM uriDRM : " + uriDRM + ", muri : " + mUri);
        this.mUriDRM = uriDRM;
    }

    public Uri getUriDRM(){ return mUriDRM; }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getBookCode() {
        return bookCode;
    }
    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getTitle2() {      return title; }
    public void setTitle2(String title){
        this.title = title;
    }

    public String getAlbumTitle2() {
        return albumTitle;
    }
    public void setAlbumTitle2(String albumTitle){
        this.albumTitle = albumTitle;
    }

    public boolean getIsFileExists() { return isFileExists;    }
    public void setIsFileExists(boolean isFileExists){
        this.isFileExists = isFileExists;
    }

    public String getServiceTP() {
        return serviceTP;
    }
    public void setServiceTP(String serviceTP){
        this.serviceTP = serviceTP;
    }

    public boolean getIsCheck() {
        return isCheck;
    }
    public void setIsCheck(boolean isCheck){
        this.isCheck = isCheck;
    }

    public String getCompose() {
        return compose;
    }
    public void setCompose(String compose){
        this.compose = compose;
    }

    public String getLyrics() {
        return lyrics;
    }
    public void setLyrics(String lyrics){
        this.lyrics = lyrics;
    }

    public String getExpireDate() {
        return expireDate;
    }
    public void setExpireDate(String expireDate){
        this.expireDate = expireDate;
    }

    public long getStartPosition() {
        return startPosition;
    }
    public void setStartPosition(long startPosition){
        this.startPosition = startPosition;
    }

    public boolean getIsFirstPlay() {
        return isFirstPlay;
    }
    public void setIsFirstPlay(boolean isFirstPlay){
        this.isFirstPlay = isFirstPlay;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }
    public void setThumbnailURL(String thumbnailURL){
        this.thumbnailURL = thumbnailURL;
    }

    public String getThumbnailModified() {
        return thumbnailModified;
    }
    public void setThumbnailModified(String thumbnailModified){
        this.thumbnailModified = thumbnailModified;
    }

    public String getDRMYN() {
        return drmYN;
    }
    public void setDRMYN(String drmYN){
        this.drmYN = drmYN;
    }

    public String getContentsCode() {
        return contentsCode;
    }

    public void setContentsCode(String contentsCode) {
        this.contentsCode = contentsCode;
    }

    public String getExhibitionType() {
        return exhibitionType;
    }

    public void setExhibitionType(String exhibitionType) {
        this.exhibitionType = exhibitionType;
    }

    public String getExhibitionCode() {
        return exhibitionCode;
    }

    public void setExhibitionCode(String exhibitionCode) {
        this.exhibitionCode = exhibitionCode;
    }

    public void setMediaTP(String mediaTP){
        this.mediaTP = mediaTP;
    }

    public String getMediaTP() {
        return mediaTP;
    }

    public void setMediaFM(String mediaFM){
        this.mediaFM = mediaFM;
    }

    public String getMediaFM() {
        return mediaFM;
    }

    public void setRelationGoodsCode(String relationGoodsCode){
        this.relationGoodsCode = relationGoodsCode;
    }
    public String getRelationGoodsCode() {
        return relationGoodsCode;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getmAfterReadingDoFlag(){ return mAfterReadingDoFlag;}

    public void setmAfterReadingDoFlag(String mAfterReadingDoFlag) {
        this.mAfterReadingDoFlag = mAfterReadingDoFlag;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public synchronized Uri getUri() {
        return mUri;
    }

    public MediaWrapper(Uri uri) {
        super();
        if (uri == null)
            throw new NullPointerException("uri was null");

        mUri = uri;

    }
}
