package cn.momia.service.course.order;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OrderService {
    long add(Order order);
    Order get(long orderId);
    List<Order> list(Collection<Long> orderIds);
    boolean delete(long userId, long orderId);
    boolean refund(long userId, long orderId);

    long queryCountByUser(long userId, int status);
    List<Order> queryByUser(long userId, int status, int start, int count);

    long queryBookableCountByUserAndOrder(long userId, long orderId);
    List<OrderPackage> queryBookableByUserAndOrder(long userId, long orderId, int start, int count);
    long queryBookableCountByUser(long userId);
    List<OrderPackage> queryBookableByUser(long userId, int start, int count);
    List<OrderPackage> queryAllBookableByUser(long userId);

    Map<Long,Long> queryBookablePackageIds(Set<Long> userIds, long subjectId);

    OrderPackage getOrderPackage(long packageId);
    Set<Integer> getOrderPackageTypes(long orderId);
    List<OrderPackage> getOrderPackages(long orderId);

    boolean isUsed(long packageId);
    boolean isGift(long fromUserId, long packageId);
    boolean isGift(long fromUserId, long toUserId, long packageId);
    boolean sendGift(long fromUserId, long packageId);
    boolean isGiftFrom(long fromUserId, long packageId);
    boolean isGiftTo(long toUserId, long packageId);
    boolean isGiftReceived(long packageId);
    boolean isGiftExpired(long packageId);
    boolean receiveGift(long fromUserId, long toUserId, long packageId);

    boolean extendPackageTime(long packageId, int newTime, int newTimeUnit);

    boolean prepay(long orderId);
    boolean pay(Payment payment);

    boolean decreaseBookableCount(long packageId);
    boolean increaseBookableCount(long packageId);

    boolean hasTrialOrder(long userId);
    int getBoughtCount(long userId, long skuId);

    Map<Long, Date> queryStartTimesOfPackages(Collection<Long> packageIds);
    List<Long> queryBookableUserIds();
    List<Long> queryUserIdsOfPackagesToExpired(int days);
}
