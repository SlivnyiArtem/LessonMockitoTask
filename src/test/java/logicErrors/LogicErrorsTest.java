package logicErrors;

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;
import shopping.Cart;
import shopping.ShoppingService;
import shopping.ShoppingServiceImpl;

import java.util.Map;

public class LogicErrorsTest {

    ShoppingService shoppingService;

    @Mock
    private ProductDao productDaoMock;

    @BeforeEach
    public void init(){
        productDaoMock = Mockito.mock(ProductDao.class);
        this.shoppingService = new ShoppingServiceImpl(productDaoMock);
    }

    /**
     * Logic Errors
     */

    /**
     * добавление отрицательного числа продуктов в карзину
     */
    @Test
    void excWhenAddNegativeNumber(){
        Customer customer = new Customer(1L, "11-11-11");
        Cart customerCart = shoppingService.getCart(customer);
        Product product = new Product("productName", 5);
        Assertions.assertThrows(IllegalArgumentException.class, () -> customerCart.add(product, -2));
    }

    /**
     * Замена числа продуктов на отрицательное кол-во в корзине
     */
    @Test
    void excWhenEditProductNegativeNumber(){
        Customer customer = new Customer(1L, "11-11-11");
        Cart customerCart = shoppingService.getCart(customer);
        Product product = new Product("productName", 5);
        customerCart.add(product, 2);
        Assertions.assertThrows(IllegalArgumentException.class, () -> customerCart.edit(product, -3));
    }

    /**
     * Последовательное добавление одного и того же продукта должно суммироваться
     */
    @Test
    void canAddMoreProducts(){
        Customer customer = new Customer(1L, "11-11-11");
        Cart customerCart = shoppingService.getCart(customer);
        Product product = new Product("productName", 5);
        customerCart.add(product, 2);
        customerCart.add(product, 2);
        Map<Product, Integer> expectedMap = Map.of(product, 4);
        Assertions.assertEquals(expectedMap, customerCart.getProducts());
    }
}
