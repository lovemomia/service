package cn.momia.service.course.activity;

import cn.momia.api.course.activity.Activity;
import cn.momia.api.course.activity.ActivityEntry;

public interface ActivityService {
    Activity getActivity(int activityId);
    ActivityEntry getActivityEntry(long entryId);
    ActivityEntry getActivityEntry(int activityId, String mobile, String childName);

    boolean joined(int activityId, String mobile, String childName);
    long join(int activityId, String mobile, String childName, String relation, int status);

    boolean prepay(long entryId);
    boolean pay(Payment payment);
}
