package cn.momia.service.promo.impl;

import cn.momia.service.promo.banner.Banner;
import cn.momia.service.promo.banner.BannerService;
import cn.momia.service.promo.PromoServiceFacade;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.CouponService;
import cn.momia.service.promo.coupon.UserCoupon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PromoServiceFacadeImpl implements PromoServiceFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(PromoServiceFacadeImpl.class);

    private static final int MAX_BANNER_COUNT = 20;

    private BannerService bannerService;
    private CouponService couponService;

    public void setBannerService(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    public void setCouponService(CouponService couponService) {
        this.couponService = couponService;
    }

    @Override
    public List<Banner> getBanners(int cityId, int count) {
        if (cityId < 0 || count <= 0 || count > MAX_BANNER_COUNT) return new ArrayList<Banner>();
        return bannerService.getBanners(cityId, count);
    }

    @Override
    public long getUserRegisterCoupon(long userId) {
        if (userId <= 0) return 0;
        return couponService.getUserRegisterCoupon(userId);
    }

    @Override
    public Coupon getCoupon(long userId, long orderId, long userCouponId) {
        if (userId <= 0 || orderId <= 0 || userCouponId <= 0) return Coupon.NOT_EXIST_COUPON;

        UserCoupon userCoupon = couponService.getUserCoupon(userId, orderId, userCouponId);
        if (!userCoupon.exists()) return Coupon.NOT_EXIST_COUPON;

        return couponService.getCoupon(userCoupon.getCouponId());
    }

    @Override
    public boolean canUse(BigDecimal totalFee, Coupon coupon) {
        return coupon.getConsumption().compareTo(totalFee) <= 0;
    }

    @Override
    public BigDecimal calcTotalFee(BigDecimal totalFee, Coupon coupon) {
        return couponService.calcTotalFee(totalFee, coupon);
    }

    @Override
    public int queryUserCouponCount(long userId, long orderId, int status) {
        if (userId <= 0) return 0;
        return couponService.queryCountByUser(userId, orderId, status);
    }

    @Override
    public List<UserCoupon> queryUserCoupon(long userId, long orderId, int status, int start, int count) {
        if (userId <= 0) return new ArrayList<UserCoupon>();
        return couponService.queryByUser(userId, orderId, status, start, count);
    }

    @Override
    public List<Coupon> getCoupons(Collection<Integer> couponIds) {
        return couponService.getCoupons(couponIds);
    }

    @Override
    public UserCoupon getNotUsedUserCouponByOrder(long orderId) {
        return couponService.getNotUsedUserCouponByOrder(orderId);
    }

    @Override
    public boolean lockUserCoupon(long userId, long orderId, long userCouponId) {
        if (userId <= 0 || orderId <= 0 || userCouponId <= 0) return true;
        return couponService.lockUserCoupon(userId, orderId, userCouponId);
    }

    @Override
    public boolean useUserCoupon(long userId, long orderId, long userCouponId) {
        if (userId <= 0 || orderId <= 0 || userCouponId <= 0) return true;
        return couponService.useUserCoupon(userId, orderId, userCouponId);
    }

    @Override
    public boolean releaseUserCoupon(long userId, long orderId) {
        if (userId <= 0 || orderId <= 0) return true;

        try {
            if (!couponService.releaseUserCoupon(userId, orderId)) {
                LOGGER.error("fail to release user coupon of order: {}", orderId);
                return false;
            }

            return true;
        } catch (Exception e) {
            LOGGER.error("fail to release user coupon of order: {}", orderId, e);
        }

        return false;
    }
}
