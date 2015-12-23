package cn.momia.api.teacher.dto;

public class TeacherStatus {
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
