package cn.momia.service.course.activity;

public interface ActivityService {
    Activity getActivity(int activityId);
    ActivityEntry getActivityEntry(long entryId);

    boolean joined(int activityId, String mobile, String childName);
    long join(int activityId, String mobile, String childName, int status);

    boolean prepay(long entryId);
    boolean pay(Payment payment);
}
