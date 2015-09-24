package cn.momia.service.product.web.ctrl;

import cn.momia.api.product.dto.ProductDto;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.api.dto.PagedList;
import cn.momia.service.product.facade.Product;
import cn.momia.service.favorite.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
public class FavoriteController extends ProductRelatedController {
    @Autowired private FavoriteService favoriteService;

    @RequestMapping(value = "/favorite", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam(value = "uid") long userId, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        long totalCount = favoriteService.queryCount(userId);
        List<Long> productIds = favoriteService.query(userId, start, count);
        List<Product> products = productServiceFacade.list(productIds);

        return MomiaHttpResponse.SUCCESS(buildPagedFavoriteDtos(totalCount, products, start, count));
    }

    private PagedList<ProductDto> buildPagedFavoriteDtos(long totalCount, List<Product> products, int start, int count) {
        PagedList<ProductDto> favoriteDtos = new PagedList(totalCount, start, count);
        List<ProductDto> productDtos = new ArrayList<ProductDto>();
        for (Product product : products) {
            productDtos.add(buildProductDto(product, Product.Type.BASE, false));
        }
        favoriteDtos.setList(productDtos);

        return favoriteDtos;
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
