package cn.momia.service.promo.impl;

import cn.momia.service.promo.banner.Banner;
import cn.momia.service.promo.banner.BannerService;
import cn.momia.service.promo.PromoServiceFacade;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.CouponService;
import cn.momia.service.promo.coupon.UserCoupon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PromoServiceFacadeImpl implements PromoServiceFacade {
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
        if (cityId < 0 || count <= 0) return new ArrayList<Banner>();
        return bannerService.getBanners(cityId, count);
    }

    @Override
    public long getUserRegisterCoupon(long userId) {
        if (userId <= 0) return 0;
        return couponService.getUserRegisterCoupon(userId);
    }

    @Override
    public UserCoupon getUserCoupon(long userId, long orderId, long userCouponId) {
        if (userId <= 0 || orderId <= 0 || userCouponId <= 0) return UserCoupon.NOT_EXIST_USER_COUPON;
        return couponService.getUserCoupon(userId, orderId, userCouponId);
    }

    @Override
    public Coupon getCoupon(int couponId) {
        if (couponId <= 0) return Coupon.NOT_EXIST_COUPON;
        return couponService.getCoupon(couponId);
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
    public Map<Integer, Coupon> getCoupons(Collection<Integer> couponIds) {
        return couponService.getCoupons(couponIds);
    }
}
