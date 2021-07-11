package com.lyp.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @author 李宜鹏
 * @date 2021-06-27 17:04
 */
public class Borrw {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String cho;
    private String bno;
    private String rdate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCho() {
        return cho;
    }

    public void setCho(String cho) {
        this.cho = cho;
    }

    public String getBno() {
        return bno;
    }

    public void setBno(String bno) {
        this.bno = bno;
    }

    public String getRdate() {
        return rdate;
    }

    public void setRdate(String rdate) {
        this.rdate = rdate;
    }

    @Override
    public String toString() {
        return "Borrw{" +
                "id=" + id +
                ", cho='" + cho + '\'' +
                ", bno='" + bno + '\'' +
                ", rdate='" + rdate + '\'' +
                '}';
    }
}
