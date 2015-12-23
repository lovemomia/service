package cn.momia.api.teacher.dto;

public class TeacherStatus {
    public static class Status {
        public static final int NOT_EXIST = 0;
        public static final int PASSED = 1;
        public static final int NOT_CHECKED = 2;
        public static final int REJECTED = 3;
    }

    public static final TeacherStatus NOT_EXIST_TEACHER_STATUS = new TeacherStatus();

    private int status;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean exists() {
        return status > 0;
    }
}
