package cn.momia.service.base.product.sku;

public class Pair<L, R> {
    private L left;
    private R right;

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
}
