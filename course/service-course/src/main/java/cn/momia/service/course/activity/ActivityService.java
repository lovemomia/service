package cn.momia.service.course.activity;

public interface ActivityService {
    Activity getActivity(int activityId);
    ActivityEntry getActivityEntry(long entryId);

    boolean prepay(long entryId);
    boolean pay(Payment payment);
}
