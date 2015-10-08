package cn.momia.service.product.web.ctrl;

import cn.momia.api.product.dto.ProductDto;
import cn.momia.api.product.dto.ProductsOfDayDto;
import cn.momia.api.product.dto.SkuDto;
import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.api.dto.PagedList;
import cn.momia.service.product.base.ProductSort;
import cn.momia.service.product.facade.Product;
import cn.momia.service.product.sku.Sku;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
public class ProductController extends ProductRelatedController {
    private static final Pattern SORT_PATTERN = Pattern.compile("(ASC|DESC)\\(([a-zA-Z0-9]+)\\)");

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam(value = "pids") String pids, @RequestParam int type) {
        Set<Long> ids = new HashSet<Long>();
        for (String id : Splitter.on(",").trimResults().omitEmptyStrings().split(pids)) ids.add(Long.valueOf(id));

        List<Product> products = productServiceFacade.list(ids);
        return MomiaHttpResponse.SUCCESS(buildProductDtos(products, type, true));
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse query(@RequestParam(value = "city") int cityId,
                                   @RequestParam int start,
                                   @RequestParam int count,
                                   @RequestParam(required = false) String sort) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = productServiceFacade.queryCount(cityId);
        List<Product> products = productServiceFacade.query(cityId, start, count, parseSort(sort));

        return MomiaHttpResponse.SUCCESS(buildPagedProductDtos(totalCount, products, start, count));
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

    private PagedList<ProductDto> buildPagedProductDtos(long totalCount, List<Product> products, int start, int count) {
        PagedList<ProductDto> pagedProductDtos = new PagedList(totalCount, start, count);
        List<ProductDto> productDtos = new ArrayList<ProductDto>();
        for (Product product : products) {
            productDtos.add(buildProductDto(product, Product.Type.BASE, false));
        }
        pagedProductDtos.setList(productDtos);

        return pagedProductDtos;
    }

    @RequestMapping(value = "/weekend", method = RequestMethod.GET)
    public MomiaHttpResponse queryByWeekend(@RequestParam(value = "city") int cityId,
                                            @RequestParam int start,
                                            @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = productServiceFacade.queryCountByWeekend(cityId);
        List<Product> products = productServiceFacade.queryByWeekend(cityId, start, count);

        return MomiaHttpResponse.SUCCESS(buildWeekendProductDtos(totalCount, products, start, count));
    }

    private PagedList<ProductDto> buildWeekendProductDtos(long totalCount, List<Product> products, int start, int count) {
        PagedList<ProductDto> pagedProductDtos = new PagedList(totalCount, start, count);
        List<ProductDto> productDtos = new ArrayList<ProductDto>();
        for (Product product : products) {
            ProductDto productDto = buildProductDto(product, Product.Type.BASE, false);
            productDto.setScheduler(product.getWeekendScheduler());
            productDtos.add(productDto);
        }
        pagedProductDtos.setList(productDtos);

        return pagedProductDtos;
    }

    @RequestMapping(value = "/month", method = RequestMethod.GET)
    public MomiaHttpResponse queryByMonth(@RequestParam(value = "city") int cityId, @RequestParam int month) {
        List<Product> products = productServiceFacade.queryByMonth(cityId, formatCurrentMonth(month), formatNextMonth(month));
        return MomiaHttpResponse.SUCCESS(buildProductsOfDayDtos(month, products));
    }

    private String formatCurrentMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        if (month < currentMonth) return String.format("%d-%02d", currentYear + 1, month);
        return String.format("%d-%02d", currentYear, month);
    }

    private String formatNextMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        int nextMonth = month + 1;
        nextMonth = nextMonth > 12 ? nextMonth - 12 : nextMonth;

        if (month < currentMonth || nextMonth < month) return String.format("%d-%02d", currentYear + 1, nextMonth);
        return String.format("%d-%02d", currentYear, nextMonth);
    }

    private List<ProductsOfDayDto> buildProductsOfDayDtos(int month, List<Product> products) {
        try {
            List<ProductsOfDayDto> productsOfDayDtos = new ArrayList<ProductsOfDayDto>();

            DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
            DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date now = new Date();
            Date currentMonth = monthFormat.parse(formatCurrentMonth(month));

            Date start = now.before(currentMonth) ? currentMonth : now;
            Date end = monthFormat.parse(formatNextMonth(month));

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
                            productsOfDayDtos.add(productsOfDayDto);
                        }
                        ProductDto productDto = buildProductDto(product, Product.Type.BASE, false);
                        productDto.setScheduler(sku.getFormatedTime());
                        if (productsOfDayDto.getProducts().size() < pageSize) productsOfDayDto.addProduct(productDto);
                    }
                }
            }

            Collections.sort(productsOfDayDtos, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    ProductsOfDayDto productsOfDayDto1 = (ProductsOfDayDto) o1;
                    ProductsOfDayDto productsOfDayDto2 = (ProductsOfDayDto) o2;

                    return productsOfDayDto1.getDate().compareTo(productsOfDayDto2.getDate());
                }
            });

            return productsOfDayDtos;
        } catch (ParseException e) {
            throw new MomiaFailedException("获取数据失败");
        }
    }

    @RequestMapping(value = "/leader", method = RequestMethod.GET)
    public MomiaHttpResponse queryNeedLeader(@RequestParam(value = "city") int cityId,
                                             @RequestParam int start,
                                             @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = productServiceFacade.queryCountNeedLeader(cityId);
        List<Product> products = productServiceFacade.queryNeedLeader(cityId, start, count);

        return MomiaHttpResponse.SUCCESS(buildPagedProductDtos(totalCount, products, start, count));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable long id, @RequestParam(defaultValue = "" + Product.Type.BASE) int type) {
        Product product = productServiceFacade.get(id);
        if (!product.exists()) return MomiaHttpResponse.FAILED("活动不存在");

        ProductDto productDto;
        switch (type) {
            case Product.Type.BASE_WITH_SKU:
                productDto = buildProductDto(product, type, true);
                break;
            default: productDto = buildProductDto(product, type, false);
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
            case Sku.Status.AVALIABLE:
                skus = Sku.sort(Sku.filterUnavaliable(skus));
                break;
            default:
                skus = Sku.sort(Sku.filterFinished(skus));
        }

        return MomiaHttpResponse.SUCCESS(buildFullSkuDtos(skus));
    }

    @RequestMapping(value = "/{id}/sku/{sid}", method = RequestMethod.GET)
    public MomiaHttpResponse getSku(@PathVariable long id, @PathVariable(value = "sid") long skuId) {
        Sku sku = productServiceFacade.getSku(skuId);
        if (sku.getProductId() != id) return MomiaHttpResponse.FAILED("无效的SKU");
        return MomiaHttpResponse.SUCCESS(buildFullSkuDto(sku));
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

        List<SkuDto> skuDtos = new ArrayList<SkuDto>();
        for (Sku sku : skus) skuDtos.add(buildBaseSkuDto(sku));

        return MomiaHttpResponse.SUCCESS(skuDtos);
    }

    @RequestMapping(value = "/{id}/sku/{sid}/leader/apply", method = RequestMethod.POST)
    public MomiaHttpResponse applyLeader(@RequestParam(value = "uid") long userId, @PathVariable long id, @PathVariable(value = "sid") long skuId){
        if (!productServiceFacade.addSkuLeader(userId, id, skuId)) return MomiaHttpResponse.FAILED("无法申请，或已经有人在您前面申请");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/led/list", method = RequestMethod.GET)
    public MomiaHttpResponse getLedProducts(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = productServiceFacade.queryCountOfLedSkus(userId);
        List<Sku> ledSkus = productServiceFacade.queryLedSkus(userId, start, count);

        List<Long> productIds = new ArrayList<Long>();
        for (Sku sku : ledSkus) productIds.add(sku.getProductId());

        List<Product> products = productServiceFacade.list(productIds);
        Map<Long, Product> productsMap = new HashMap<Long, Product>();
        for (Product product : products) {
            productsMap.put(product.getId(), product);
        }

        PagedList<ProductDto> pagedProductDtos = new PagedList(totalCount, start, count);
        List<ProductDto> productDtos = new ArrayList<ProductDto>();
        for (Sku sku : Sku.sortByStartTime(ledSkus)) {
            Product product = productsMap.get(sku.getProductId());
            if (product == null) continue;

            ProductDto productDto = buildProductDto(product, Product.Type.BASE, false);
            productDto.setScheduler(sku.getFormatedTime());
            productDtos.add(productDto);
        }
        pagedProductDtos.setList(productDtos);

        return MomiaHttpResponse.SUCCESS(pagedProductDtos);
    }
}
