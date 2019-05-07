package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {


    private BookKeeper bookKeeper;
    private ClientData clientData;
    private InvoiceRequest invoiceRequest;

    @Before
    public void init() {
        bookKeeper = new BookKeeper(new InvoiceFactory());
        clientData = new ClientData(Id.generate(), "clientName");
        invoiceRequest = new InvoiceRequest(clientData);

    }

    @Test
    public void invoiceRequestWithOnePositionShouldReturnInvoiceWithOnePosition() {
        Money money = new Money(1);

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.FOOD);

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(productData.getType(), money)).thenReturn(new Tax(money, "description"));

        RequestItem requestItem = new RequestItem(productData, 1, money);
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat("should return 1", invoice.getItems().size(), is(equalTo(1)));
    }

    @Test
    public void invoiceRequestWithZeroPositionShouldReturnInvoiceWithZeroPosition() {
        Money money = new Money(1);

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.FOOD);

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(productData.getType(), money)).thenReturn(new Tax(money, "description"));

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat("should return 0", invoice.getItems().size(), is(equalTo(0)));
    }


    @Test
    public void invoiceRequestWithOnePositionShouldReturnInformationAboutMoneyInInvoice() {
        Money money = new Money(1);

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.FOOD);

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(productData.getType(), money)).thenReturn(new Tax(money, "description"));

        RequestItem requestItem = new RequestItem(productData, 1, money);
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat("should return " + money, invoice.getItems().get(0).getNet(), is(equalTo(money)));
    }

    @Test
    public void invoiceRequestWithTwoPositionShouldReturnInvoiceWithTwoPosition() {
        Money money = new Money(1);

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.FOOD);

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(productData.getType(), money)).thenReturn(new Tax(money, "description"));

        RequestItem requestItem = new RequestItem(productData, 1, money);
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(2)).calculateTax(productData.getType(), money);
    }


}
