package cn.momia.service.product.web.ctrl;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.TimeUtil;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.webapp.ctrl.dto.ListDto;
import cn.momia.common.webapp.ctrl.dto.PagedListDto;
import cn.momia.service.product.base.ProductSort;
import cn.momia.service.product.facade.Product;
import cn.momia.service.product.facade.ProductServiceFacade;
import cn.momia.service.product.sku.Sku;
import cn.momia.service.product.web.ctrl.dto.BaseProductDto;
import cn.momia.service.product.web.ctrl.dto.BaseSkuDto;
import cn.momia.service.product.web.ctrl.dto.FullProductDto;
import cn.momia.service.product.web.ctrl.dto.FullSkuDto;
import cn.momia.service.product.web.ctrl.dto.MiniProductDto;
import cn.momia.service.product.web.ctrl.dto.ProductDto;
import cn.momia.service.product.web.ctrl.dto.ProductsOfDayDto;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {
    private static final Pattern SORT_PATTERN = Pattern.compile("(ASC|DESC)\\(([a-zA-Z0-9]+)\\)");

    @Autowired private ProductServiceFacade productServiceFacade;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam(value = "pids") String pids) {
        Set<Long> ids = new HashSet<Long>();
        for (String id : Splitter.on(",").trimResults().omitEmptyStrings().split(pids)) ids.add(Long.valueOf(id));

        List<Product> products = productServiceFacade.list(ids);
        return MomiaHttpResponse.SUCCESS(BaseProductDto.toDtos(products, true));
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse query(@RequestParam(value = "city") int cityId,
                                   @RequestParam int start,
                                   @RequestParam int count,
                                   @RequestParam(required = false) String sort) {
        if (cityId < 0 || isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedListDto.EMPTY);

        long totalCount = productServiceFacade.queryCount(cityId);
        List<Product> products = productServiceFacade.query(cityId, start, count, parseSort(sort));

        return MomiaHttpResponse.SUCCESS(buildProductsDto(totalCount, products, start, count));
    }

    private ProductSort parseSort(String sort) {
        if (StringUtils.isBlank(sort)) return ProductSort.DEFAULT;

        ProductSort productSort = new ProductSort();
        String[] sorts = StringUtils.split(sort, ",");
        for (String s : sorts) {
            Matcher matcher = SORT_PATTERN.matcher(s);
            if (matcher.find()) productSort.addSort(matcher.group(2), matcher.group(1));
        }

        return productSort;
    }

    private PagedListDto buildProductsDto(long totalCount, List<Product> products, int start, int count) {
        PagedListDto productsDto = new PagedListDto(totalCount, start, count);
        ListDto baseProductsDto = new ListDto();
        for (Product product : products) {
            baseProductsDto.add(new BaseProductDto(product));
        }
        productsDto.addAll(baseProductsDto);

        return productsDto;
    }

    @RequestMapping(value = "/weekend", method = RequestMethod.GET)
    public MomiaHttpResponse queryByWeekend(@RequestParam(value = "city") int cityId,
                                            @RequestParam int start,
                                            @RequestParam int count) {
        if (cityId <0 || isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedListDto.EMPTY);

        long totalCount = productServiceFacade.queryCountByWeekend(cityId);
        List<Product> products = productServiceFacade.queryByWeekend(cityId, start, count);

        return MomiaHttpResponse.SUCCESS(buildWeekendProductsDto(totalCount, products, start, count));
    }

    private PagedListDto buildWeekendProductsDto(long totalCount, List<Product> products, int start, int count) {
        PagedListDto productsDto = new PagedListDto(totalCount, start, count);
        ListDto baseProductsDto = new ListDto();
        for (Product product : products) {
            BaseProductDto baseProductDto = new BaseProductDto(product);
            baseProductDto.setScheduler(product.getWeekendScheduler());
            baseProductsDto.add(baseProductDto);
        }
        productsDto.addAll(baseProductsDto);

        return productsDto;
    }

    @RequestMapping(value = "/month", method = RequestMethod.GET)
    public MomiaHttpResponse queryByMonth(@RequestParam(value = "city") int cityId, @RequestParam int month) {
        List<Product> products = productServiceFacade.queryByMonth(cityId, month);

        return MomiaHttpResponse.SUCCESS(buildGroupedProductsDto(month, products));
    }

    private ListDto buildGroupedProductsDto(int month, List<Product> products) {
        try {
            ListDto productsDto = new ListDto();

            DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
            DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date now = new Date();
            Date currentMonth = monthFormat.parse(TimeUtil.formatYearMonth(month));

            Date start = now.before(currentMonth) ? currentMonth : now;
            Date end = monthFormat.parse(TimeUtil.formatNextYearMonth(month));

            int pageSize = Configuration.getInt("Product.Month.PageSize");
            Map<String, ProductsOfDayDto> productsOfDayDtoMap = new HashMap<String, ProductsOfDayDto>();
            for (Product product : products) {
                for (Sku sku : product.getSkus()) {
                    Date startTime = sku.getStartTime();
                    if (startTime.after(start) && startTime.before(end)) {
                        String day = dayFormat.format(startTime);
                        ProductsOfDayDto productsOfDayDto = productsOfDayDtoMap.get(day);
                        if (productsOfDayDto == null) {
                            productsOfDayDto = new ProductsOfDayDto();
                            productsOfDayDtoMap.put(day, productsOfDayDto);

                            productsOfDayDto.setDate(startTime);
                            productsDto.add(productsOfDayDto);
                        }
                        BaseProductDto baseProductDto = new BaseProductDto(product);
                        baseProductDto.setScheduler(sku.getFormatedTime());
                        if (productsOfDayDto.getProducts().size() < pageSize) productsOfDayDto.addProduct(baseProductDto);
                    }
                }
            }

            Collections.sort(productsDto, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    ProductsOfDayDto productsOfDayDto1 = (ProductsOfDayDto) o1;
                    ProductsOfDayDto productsOfDayDto2 = (ProductsOfDayDto) o2;

                    return productsOfDayDto1.getDate().compareTo(productsOfDayDto2.getDate());
                }
            });

            return productsDto;
        } catch (ParseException e) {
            throw new MomiaFailedException("获取数据失败");
        }
    }

    @RequestMapping(value = "/leader", method = RequestMethod.GET)
    public MomiaHttpResponse queryNeedLeader(@RequestParam(value = "city") int cityId,
                                             @RequestParam int start,
                                             @RequestParam int count) {
        if (cityId < 0 || isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedListDto.EMPTY);

        long totalCount = productServiceFacade.queryCountNeedLeader(cityId);
        List<Product> products = productServiceFacade.queryNeedLeader(cityId, start, count);

        return MomiaHttpResponse.SUCCESS(buildProductsDto(totalCount, products, start, count));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable long id, @RequestParam(defaultValue = "" + Product.Type.BASE) int type) {
        Product product = productServiceFacade.get(id);
        if (!product.exists()) return MomiaHttpResponse.FAILED("活动不存在");

        ProductDto productDto;
        switch (type) {
            case Product.Type.MINI:
                productDto = new MiniProductDto(product);
                break;
            case Product.Type.BASE_WITH_SKU:
                productDto = new BaseProductDto(product, true);
                break;
            case Product.Type.FULL:
                productDto = new FullProductDto(product);
                break;
            default: productDto = new BaseProductDto((product));
        }

        return MomiaHttpResponse.SUCCESS(productDto);
    }

    @RequestMapping(value = "/{id}/detail", method = RequestMethod.GET)
    public MomiaHttpResponse getDetail(@PathVariable long id) {
        String productDetail = productServiceFacade.getDetail(id);
        if (StringUtils.isBlank(productDetail)) return MomiaHttpResponse.FAILED("活动详情不存在");
        return MomiaHttpResponse.SUCCESS(productDetail);
    }

    @RequestMapping(value = "/{id}/sold", method = RequestMethod.POST)
    public MomiaHttpResponse sold(@PathVariable long id, @RequestParam int count) {
        if (!productServiceFacade.sold(id, count)) return MomiaHttpResponse.FAILED;
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public MomiaHttpResponse listSkus(@PathVariable long id, @RequestParam(required = false, defaultValue = "" + Sku.Status.AVALIABLE) int status) {
        List<Sku> skus = productServiceFacade.listSkus(id);
        switch (status) {
            case Sku.Status.ALL:
                skus = Sku.sortByStartTime(skus);
                break;
            default:
                skus = Sku.sort(Sku.filterFinished(skus));
        }

        return MomiaHttpResponse.SUCCESS(buildSkusDto(skus));
    }

    private ListDto buildSkusDto(List<Sku> skus) {
        ListDto skusDto = new ListDto();
        for (Sku sku : skus) {
            skusDto.add(new FullSkuDto(sku));
        }

        return skusDto;
    }

    @RequestMapping(value = "/{id}/sku/{sid}", method = RequestMethod.GET)
    public MomiaHttpResponse getSku(@PathVariable long id, @PathVariable(value = "sid") long skuId) {
        Sku sku = productServiceFacade.getSku(skuId);
        if (sku.getProductId() != id) return MomiaHttpResponse.FAILED("无效的SKU");
        return MomiaHttpResponse.SUCCESS(new FullSkuDto(sku));
    }

    @RequestMapping(value = "/{id}/sku/{sid}/lock", method = RequestMethod.POST)
    public MomiaHttpResponse lock(@PathVariable long id,
                                  @PathVariable(value = "sid") long skuId,
                                  @RequestParam int count,
                                  @RequestParam(value = "joined") int joinedCount) {
        return MomiaHttpResponse.SUCCESS(productServiceFacade.lockStock(id, skuId, count, joinedCount));
    }

    @RequestMapping(value = "/{id}/sku/{sid}/unlock", method = RequestMethod.POST)
    public MomiaHttpResponse unlock(@PathVariable long id,
                                    @PathVariable(value = "sid") long skuId,
                                    @RequestParam int count,
                                    @RequestParam(value = "joined") int joinedCount) {
        return MomiaHttpResponse.SUCCESS(productServiceFacade.unlockStock(id, skuId, count, joinedCount));
    }

    @RequestMapping(value = "/{id}/sku/leader", method = RequestMethod.GET)
    public MomiaHttpResponse listSkusWithLeaders(@PathVariable long id) {
        List<Sku> skus = Sku.sortByStartTime(Sku.filterClosed(productServiceFacade.listSkus(id)));

        ListDto skusDto = new ListDto();
        for (Sku sku : skus) skusDto.add(new BaseSkuDto(sku));

        return MomiaHttpResponse.SUCCESS(skusDto);
    }

    @RequestMapping(value = "/{id}/sku/{sid}/leader/apply", method = RequestMethod.POST)
    public MomiaHttpResponse applyLeader(@RequestParam(value = "uid") long userId, @PathVariable long id, @PathVariable(value = "sid") long skuId){
        if (!productServiceFacade.addSkuLeader(userId, id, skuId)) return MomiaHttpResponse.FAILED("无法申请，或已经有人在您前面申请");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/led/list", method = RequestMethod.GET)
    public MomiaHttpResponse getLedProducts(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedListDto.EMPTY);

        long totalCount = productServiceFacade.queryCountOfLedSkus(userId);
        List<Sku> ledSkus = productServiceFacade.queryLedSkus(userId, start, count);

        List<Long> productIds = new ArrayList<Long>();
        for (Sku sku : ledSkus) productIds.add(sku.getProductId());

        List<Product> products = productServiceFacade.list(productIds);
        Map<Long, Product> productsMap = new HashMap<Long, Product>();
        for (Product product : products) {
            productsMap.put(product.getId(), product);
        }

        PagedListDto productsDto = new PagedListDto(totalCount, start, count);
        for (Sku sku : Sku.sortByStartTime(ledSkus)) {
            Product product = productsMap.get(sku.getProductId());
            if (product == null) continue;

            BaseProductDto baseProductDto = new BaseProductDto(product, false);
            baseProductDto.setScheduler(sku.getFormatedTime());
            productsDto.add(baseProductDto);
        }

        return MomiaHttpResponse.SUCCESS(productsDto);
    }
}
