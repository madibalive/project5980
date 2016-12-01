package com.example.madiba.venu_alpha.models;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by Madiba on 11/3/2016.
 */

public class ModelFeedItem  {
    public static final int TYPP_MEDIA_IMAGE=1;
    public static final int  TYPP_MEDIA_VIDEO=2;
    public static final int TYPP_GOSSIP=4;
    public static final int TYPP_EVENT=3;


    //Parse
    public String className=null;
    public String parseId=null;

    //General
    public int cmt = 0;
    public int shr = 0;
    public int rct = 0; // reactions
    public Date date = null;
    public String dateToString = null;
    public String hashtag = null;
    public String url = null;
    public int type = 0;
    public Boolean isShared = false;

    //user
    public String userId=null;
    public String name = null;
    public String avatar = null;



    // Peep
    public int ppVideo = 0;
    public Boolean ppIsLike = false;


    //Event
    public String evTitle = null;
    public String evLocation = null;
    public Date evDate = null;
    public String evDateToString = null;
    public Date evTime = null;
    public String evTimeToString = null;
    public Boolean evIsInterest =false;
    public String evDesc =null;
    public int mprice = 0;


    //Gossip
    public Date gpElapseTime = null;
    public String gpElapseTimeToString = null;
    public String gpTitle = null;
    public Boolean gpIsThumbsUp =false;
    public int gpChatId =0;

    public ModelFeedItem() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getParseId() {
        return parseId;
    }

    public void setParseId(String parseId) {
        this.parseId = parseId;
    }

    public int getCmt() {
        return cmt;
    }

    public void setCmt(int cmt) {
        this.cmt = cmt;
    }

    public int getShr() {
        return shr;
    }

    public void setShr(int shr) {
        this.shr = shr;
    }

    public int getRct() {
        return rct;
    }

    public void setRct(int rct) {
        this.rct = rct;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateToString() {
        return dateToString;
    }

    public void setDateToString(String dateToString) {
        this.dateToString = dateToString;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Boolean getShared() {
        return isShared;
    }

    public void setShared(Boolean shared) {
        isShared = shared;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getPpVideo() {
        return ppVideo;
    }

    public void setPpVideo(int ppVideo) {
        this.ppVideo = ppVideo;
    }

    public Boolean getPpIsLike() {
        return ppIsLike;
    }

    public void setPpIsLike(Boolean ppIsLike) {
        this.ppIsLike = ppIsLike;
    }

    public String getEvTitle() {
        return evTitle;
    }

    public void setEvTitle(String evTitle) {
        this.evTitle = evTitle;
    }

    public String getEvLocation() {
        return evLocation;
    }

    public void setEvLocation(String evLocation) {
        this.evLocation = evLocation;
    }

    public Date getEvDate() {
        return evDate;
    }

    public void setEvDate(Date evDate) {
        this.evDate = evDate;
    }

    public String getEvDateToString() {
        return evDateToString;
    }

    public void setEvDateToString(String evDateToString) {
        this.evDateToString = evDateToString;
    }

    public Date getEvTime() {
        return evTime;
    }

    public void setEvTime(Date evTime) {
        this.evTime = evTime;
    }

    public String getEvTimeToString() {
        return evTimeToString;
    }

    public void setEvTimeToString(String evTimeToString) {
        this.evTimeToString = evTimeToString;
    }

    public Boolean getEvIsInterest() {
        return evIsInterest;
    }

    public void setEvIsInterest(Boolean evIsInterest) {
        this.evIsInterest = evIsInterest;
    }

    public String getEvDesc() {
        return evDesc;
    }

    public void setEvDesc(String evDesc) {
        this.evDesc = evDesc;
    }

    public int getMprice() {
        return mprice;
    }

    public void setMprice(int mprice) {
        this.mprice = mprice;
    }

    public Date getGpElapseTime() {
        return gpElapseTime;
    }

    public void setGpElapseTime(Date gpElapseTime) {
        this.gpElapseTime = gpElapseTime;
    }

    public String getGpElapseTimeToString() {
        return gpElapseTimeToString;
    }

    public void setGpElapseTimeToString(String gpElapseTimeToString) {
        this.gpElapseTimeToString = gpElapseTimeToString;
    }

    public String getGpTitle() {
        return gpTitle;
    }

    public void setGpTitle(String gpTitle) {
        this.gpTitle = gpTitle;
    }

    public Boolean getGpIsThumbsUp() {
        return gpIsThumbsUp;
    }

    public void setGpIsThumbsUp(Boolean gpIsThumbsUp) {
        this.gpIsThumbsUp = gpIsThumbsUp;
    }

    public int getGpChatId() {
        return gpChatId;
    }

    public void setGpChatId(int gpChatId) {
        this.gpChatId = gpChatId;
    }
}
