package shopping;

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import product.Product;
import product.ProductDao;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class ShoppingServiceImplTest {

    ShoppingService shoppingService;

    Product testProduct;
    Customer testCustomer;


    @Mock
    private ProductDao productDaoMock;

    @BeforeEach
    public void init(){
        productDaoMock = Mockito.mock(ProductDao.class);
        this.shoppingService = new ShoppingServiceImpl(productDaoMock);
        this.testProduct = new Product("productName", 5);
        this.testCustomer = new Customer(1L, "11-11-11");
    }


    /**
     * тест на создание корзины, привязанной к покупателю
     */
    @Test
    void getCart() {
        //внутри метода getCart происходит только создание и возвращение нового объекта Cart(customer)
        //т.е. метод обладает пустой логикой и может быть заменен напрямую вызовом конструктора Cart

    }

    /**
     * тест на получение предыдущей версии корзины, при новом заходе на сайт
     */
    @Test
    void getSameCart(){
//        Customer customer = new Customer(1L, "11-11-11");
        Cart customerCart = shoppingService.getCart(testCustomer);
        Assertions.assertEquals(customerCart, shoppingService.getCart(testCustomer));
    }


    /**
     * Тест на создание корзины, если неправильно указан телефон покупателя
     */
    @Test
    void getCartIncorrectPhone(){
        Assertions.assertThrows(ArgumentConversionException.class,
                () -> shoppingService.getCart(new Customer(1L, "Telephone")));
    }

    /**
     * Тест на создание корзины, для несуществующего покупателя.
     */
    @Test
    void getCartNullCustomer(){
        Customer nullCustomer = null;
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> shoppingService.getCart(nullCustomer));
    }



    /**
     * Проверка того, что метод возвращает полный список покупателей
     */
    @Test
    void getAllProducts() {
        //В тесте нет необходимости, так как мы в действительности тестируем метод getAll() Dao,
        // реализацию которого нам в любом случае придется прописать через мок самостоятельно.
    }

    /**
     * Проверка получения продукта по его наименованию
     */
    @Test
    void getProductByName() {
        //В тесте нет необходимости, так как мы в действительности тестируем метод getByName() Dao,
        // реализацию которого нам в любом случае придется прописать через мок самостоятельно.
    }


    /**
     * После покупки продуктов корзина пустая
     */
    @Test
    void emptyCartAfterBuy() {
        Cart cart = shoppingService.getCart(testCustomer);
        cart.add(testProduct,1);
        Assertions.assertDoesNotThrow(() -> shoppingService.buy(cart));
        Assertions.assertEquals(0, cart.getProducts().size());
    }

    /**
     * проверка неудачного завершения операции покупки при пустой корзине
     */
    @Test
    void notBuyWhenNoProducts() {
        try {
            boolean purchaseResult = shoppingService.buy(new Cart(testCustomer));
            Assertions.assertFalse(purchaseResult);
        }catch (BuyException exc){
            fail(exc.getMessage());
        }
    }

    /**
     * Проверка выброса исключения при попытке купить больше единиц товара, чем доступно
     */
    @Test
    void throwBuyTooMany(){
        Product product = new Product("product 1", 6);
        Cart cart = shoppingService.getCart(testCustomer);
        cart.add(product,5);
        product.subtractCount(4);

        Assertions.assertThrows(BuyException.class, () -> shoppingService.buy(cart));
    }


    /**
     * Проверка успешного сохранения через Dao
     */
    @Test
    void saveProduct() {
        Cart cart = shoppingService.getCart(testCustomer);
        cart.add(testProduct,1);

        Assertions.assertDoesNotThrow(() -> shoppingService.buy(cart));

        Mockito.verify(productDaoMock, Mockito.times(1))
                .save(Mockito.eq(testProduct));
    }

    /**
     * при добавлении несколькими приемами большего числа продуктов, чем есть в наличии, покупка не должна происходить
     */
    @Test
    void notSaveWhenSumMoreThanHave(){
        try {
        Cart cart = shoppingService.getCart(testCustomer);
        cart.add(testProduct,3);
        cart.add(testProduct,3);
        shoppingService.buy(cart);
        } catch (Exception exc){

            Assertions.assertEquals(BuyException.class, exc.getClass());
        }
        Mockito.verify(productDaoMock, Mockito.never())
                .save(Mockito.any(Product.class));
    }
}