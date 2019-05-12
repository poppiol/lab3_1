package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;


@RunWith(MockitoJUnitRunner.class)
public class AddProductCommandHandlerTest {

    private AddProductCommandHandler addProductCommandHandler;
    private Product product = mock(Product.class);
    private AddProductCommand addProductCommand;
    private Reservation reservation = mock(Reservation.class);
    private ReservationRepository reservationRepository = mock(ReservationRepository.class);
    private ProductRepository productRepository = mock(ProductRepository.class);
    private SuggestionService suggestionService = mock(SuggestionService.class);
    private ClientRepository clientRepository = mock(ClientRepository.class);
    private SystemContext systemContext = mock(SystemContext.class);

    @Before
    public void init() {
        when(product.isAvailable()).thenReturn(true);
        when(reservationRepository.load(any())).thenReturn(reservation);
        when(productRepository.load(any())).thenReturn(product);

        addProductCommandHandler = new AddProductCommandHandler(reservationRepository, productRepository, suggestionService, clientRepository, systemContext);
    }

    @Test
    public void handleWhereProductIsNotAvailable() {
        when(product.isAvailable()).thenReturn(false);

        Assert.assertThat("should return false", product.isAvailable(), is(false));
    }

    @Test
    public void handleWhereProductIsAvailable() {
        Assert.assertThat("should return true", product.isAvailable(), is(true));
    }

    @Test
    public void handleReservationShouldCallingAddMethodTwoTimes() {
        addProductCommand = new AddProductCommand(new Id("1"), new Id("1"), 20);
        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);

        verify(reservation, Mockito.times(2)).add(product, addProductCommand.getQuantity());
    }

    @Test
    public void handleReservationRespositoryShouldCallingHisMethodsTwoTimes() {
        addProductCommand = new AddProductCommand(new Id("1"), new Id("1"), 20);
        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepository, Mockito.times(2)).load(addProductCommand.getOrderId());
        verify(reservationRepository, Mockito.times(2)).save(reservation);
    }
}
