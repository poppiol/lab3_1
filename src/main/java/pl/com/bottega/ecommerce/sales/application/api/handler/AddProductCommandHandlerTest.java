package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;


@RunWith(MockitoJUnitRunner.class)
public class AddProductCommandHandlerTest {

    private AddProductCommandHandler addProductCommandHandler;
    private Product product;
    private AddProductCommand addProductCommand;


    @Test
    public void handleWhereProductIsNotAvailable() {
        product = mock(Product.class);
        when(product.isAvailable()).thenReturn(false);

        Assert.assertThat("should return false", product.isAvailable(),is(false));
    }

    @Test
    public void handleWhereProductIsAvailable() {
        product = mock(Product.class);
        when(product.isAvailable()).thenReturn(true);

        Assert.assertThat("should return true", product.isAvailable(),is(true));
    }
}
