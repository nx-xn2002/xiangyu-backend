package com.nx.model.enums;

/**
 * 队伍状态枚举
 *
 * @author nx
 */
public enum TeamStatusEnum {

    /**
     * 公开状态
     */
    PUBLIC(0, "公开"),
    /**
     * 私有状态
     */
    PRIVATE(1, "私有"),
    /**
     * 加密状态
     */
    SECRET(2, "加密");

    private int value;
    private String text;

    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        TeamStatusEnum[] teamStatusEnums = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : teamStatusEnums) {
            if (teamStatusEnum.getValue() == value) {
                return teamStatusEnum;
            }
        }
        return null;
    }

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
