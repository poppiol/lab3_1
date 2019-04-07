package lab3_1;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.invoicing.BookKeeper;
import pl.com.bottega.ecommerce.sales.domain.invoicing.Invoice;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceFactory;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceRequest;
import pl.com.bottega.ecommerce.sales.domain.invoicing.RequestItem;
import pl.com.bottega.ecommerce.sales.domain.invoicing.Tax;
import pl.com.bottega.ecommerce.sales.domain.invoicing.TaxPolicy;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class BookKeeperTest {

    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;
    private TaxPolicy taxPolicy;

    @Before
    public void setUp() {
        bookKeeper = new BookKeeper(new InvoiceFactory());
        invoiceRequest = new InvoiceRequest(new ClientData());
        taxPolicy = mock(TaxPolicy.class);
    }

    @Test
    public void testInvoiceRequestWithOneProductReturnOneProductInvoice() {
        ProductData productData = new ProductData(Id.generate(), new Money(new BigDecimal(1)), "kabanos", ProductType.FOOD, new Date());
        int quantity = 10;
        RequestItem requestItem = new RequestItem(productData, quantity, productData.getPrice()
                                                                                    .multiplyBy(quantity));
        invoiceRequest.add(requestItem);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(new BigDecimal(1)), "tax"));
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoice.getItems()
                          .size(),
                is(equalTo(1)));
        assertThat(invoice.getItems()
                          .get(0)
                          .getProduct(),
                is(equalTo(productData)));
    }

    @Test
    public void testNumberOfCallsTaxCalculateForInvoiceRequestWithTwoProducts() {
        ProductData productData = new ProductData(Id.generate(), new Money(new BigDecimal(1)), "kabanos", ProductType.FOOD, new Date());
        int quantity = 10;
        RequestItem requestItem = new RequestItem(productData, quantity, productData.getPrice()
                                                                                    .multiplyBy(quantity));
        ProductData productData2 = new ProductData(Id.generate(), new Money(new BigDecimal(11)), "waciki", ProductType.STANDARD,
                new Date());
        RequestItem requestItem2 = new RequestItem(productData2, quantity - 2, productData.getPrice()
                                                                                          .multiplyBy(quantity));
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem2);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(new BigDecimal(1)), "tax"));
        bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(2)).calculateTax(any(ProductType.class), any(Money.class));
    }

    @Test
    public void testInvoiceRequestWithTwoProductsReturnTwoProductsInvoice() {
        ProductData productData = new ProductData(Id.generate(), new Money(new BigDecimal(1)), "kabanos", ProductType.FOOD, new Date());
        int quantity = 10;
        RequestItem requestItem = new RequestItem(productData, quantity, productData.getPrice()
                                                                                    .multiplyBy(quantity));
        ProductData productData2 = new ProductData(Id.generate(), new Money(new BigDecimal(11)), "waciki", ProductType.STANDARD,
                new Date());
        RequestItem requestItem2 = new RequestItem(productData2, quantity - 2, productData.getPrice()
                                                                                          .multiplyBy(quantity));
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem2);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(new BigDecimal(1)), "tax"));
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoice.getItems()
                          .size(),
                is(equalTo(2)));
        assertThat(invoice.getItems()
                          .get(0)
                          .getProduct(),
                is(equalTo(productData)));
        assertThat(invoice.getItems()
                          .get(1)
                          .getProduct(),
                is(equalTo(productData2)));
    }

    @Test
    public void testClientDataPassedToInvoice() {
        ClientData client = new ClientData(Id.generate(), "Pepsi");
        invoiceRequest = new InvoiceRequest(client);
        ProductData productData = new ProductData(Id.generate(), new Money(new BigDecimal(1)), "kabanos", ProductType.FOOD, new Date());
        int quantity = 10;
        RequestItem requestItem = new RequestItem(productData, quantity, productData.getPrice()
                                                                                    .multiplyBy(quantity));
        invoiceRequest.add(requestItem);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(new BigDecimal(1)), "tax"));
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoice.getClient()
                          .getName(),
                is(equalTo(client.getName())));
        assertThat(invoice.getClient()
                          .getAggregateId(),
                is(equalTo(client.getAggregateId())));
    }

}
