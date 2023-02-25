package dev.tim.discordbot.model;

import java.util.Date;

public class Warning {

    private final String id;
    private final long member_id;
    private final long member_warner_id;
    private final String reason;
    private final Date date;

    public Warning(String id, long member_id, long member_warner_id, String reason, Date date) {
        this.id = id;
        this.member_id = member_id;
        this.member_warner_id = member_warner_id;
        this.reason = reason;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public long getMember_id() {
        return member_id;
    }

    public long getMember_warner_id() {
        return member_warner_id;
    }

    public String getReason() {
        return reason;
    }

    public Date getDate() {
        return date;
    }
}