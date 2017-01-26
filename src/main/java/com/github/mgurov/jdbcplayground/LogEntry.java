package com.github.mgurov.jdbcplayground;

import com.github.mgurov.jdbcplayground.hibernate.StringMapToPgJsonbConverter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "LOGGING")
public class LogEntry {
    @Id
    @Column(name = "date")
    public final Date timestamp;
    @Transient
    public String remoteAddress;
    @Transient
    public String requestURI;

    private LogEntry() {
        this(null, null, null);
    }

    public LogEntry(Date timestamp, String remoteAddress, String requestURI) {
        this.timestamp = timestamp;
        this.remoteAddress = remoteAddress;
        this.requestURI = requestURI;
    }

    @Access(AccessType.PROPERTY)
    @Column(name="data")
    @Convert(converter = StringMapToPgJsonbConverter.class)
    public Map<String, String> getData() {
        Map<String, String> r = new HashMap<>();
        r.put("ip", remoteAddress);
        r.put("uri", requestURI);
        return r;
    }

    public void setData(Map<String, String> data) {
        remoteAddress = data.get("ip");
        requestURI = data.get("uri");
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "timestamp=" + timestamp +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", requestURI='" + requestURI + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogEntry logEntry = (LogEntry) o;

        if (timestamp != null ? !timestamp.equals(logEntry.timestamp) : logEntry.timestamp != null) return false;
        if (remoteAddress != null ? !remoteAddress.equals(logEntry.remoteAddress) : logEntry.remoteAddress != null)
            return false;
        return requestURI != null ? requestURI.equals(logEntry.requestURI) : logEntry.requestURI == null;
    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = 31 * result + (remoteAddress != null ? remoteAddress.hashCode() : 0);
        result = 31 * result + (requestURI != null ? requestURI.hashCode() : 0);
        return result;
    }
}
