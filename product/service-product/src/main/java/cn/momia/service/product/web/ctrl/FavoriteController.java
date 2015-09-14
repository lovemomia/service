package cn.momia.service.product.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.common.webapp.ctrl.dto.ListDto;
import cn.momia.common.webapp.ctrl.dto.PagedListDto;
import cn.momia.service.product.facade.Product;
import cn.momia.service.product.facade.ProductServiceFacade;
import cn.momia.service.favorite.FavoriteService;
import cn.momia.service.product.web.ctrl.dto.BaseProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class FavoriteController extends BaseController {
    @Autowired private FavoriteService favoriteService;
    @Autowired private ProductServiceFacade productServiceFacade;

    @RequestMapping(value = "/favorite", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedListDto.EMPTY);

        long totalCount = favoriteService.queryCount(userId);
        List<Long> productIds = favoriteService.query(userId, start, count);
        List<Product> products = productServiceFacade.list(productIds);

        return MomiaHttpResponse.SUCCESS(buildFavoritesDto(totalCount, products, start, count));
    }

    private PagedListDto buildFavoritesDto(long totalCount, List<Product> products, int start, int count) {
        PagedListDto favoritesDto = new PagedListDto(totalCount, start, count);
        ListDto baseProductsDto = new ListDto();
        for (Product product : products) {
            baseProductsDto.add(new BaseProductDto(product));
        }
        favoritesDto.addAll(baseProductsDto);

        return favoritesDto;
    }

    @RequestMapping(value = "/{id}/favor", method = RequestMethod.POST)
    public MomiaHttpResponse favor(@RequestParam(value = "uid") long userId, @PathVariable long id){
        Product product = productServiceFacade.get(id, true);
        if (!product.exists()) return MomiaHttpResponse.FAILED("添加收藏失败");

        if (!favoriteService.isFavoried(userId, id) && !favoriteService.favor(userId, id)) return MomiaHttpResponse.FAILED("添加收藏失败");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{id}/unfavor", method = RequestMethod.POST)
    public MomiaHttpResponse unfavor(@RequestParam(value = "uid") long userId, @PathVariable long id){
        if (favoriteService.isFavoried(userId, id) && !favoriteService.unFavor(userId, id)) return MomiaHttpResponse.FAILED("取消收藏失败");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{id}/favored", method = RequestMethod.GET)
    public MomiaHttpResponse favored(@RequestParam(value = "uid") long userId, @PathVariable long id){
        return MomiaHttpResponse.SUCCESS(favoriteService.isFavoried(userId, id));
    }
}
