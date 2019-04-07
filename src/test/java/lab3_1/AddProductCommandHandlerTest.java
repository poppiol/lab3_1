package lab3_1;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation.ReservationStatus;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

public class AddProductCommandHandlerTest {

    private AddProductCommandHandler productHandler;
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private SuggestionService suggestionService;
    private ClientRepository clientRepository;
    private SystemContext systemContext;
    private AddProductCommand productCommand;
    private Client client;

    @Before
    public void setUp() {
        productHandler = new AddProductCommandHandler();

        reservationRepository = mock(ReservationRepository.class);
        productRepository = mock(ProductRepository.class);
        suggestionService = mock(SuggestionService.class);
        clientRepository = mock(ClientRepository.class);
        systemContext = new SystemContext();

        productCommand = new AddProductCommand(new Id("1"), new Id("2"), 10);
        client = new Client();

        Whitebox.setInternalState(productHandler, "reservationRepository", reservationRepository);
        Whitebox.setInternalState(productHandler, "productRepository", productRepository);
        Whitebox.setInternalState(productHandler, "suggestionService", suggestionService);
        Whitebox.setInternalState(productHandler, "clientRepository", clientRepository);
        Whitebox.setInternalState(productHandler, "systemContext", systemContext);
    }

    @Test
    public void testIfProductAndReservationRepositoriesWereCalledOnce() {
        Reservation reservation = new Reservation(Id.generate(), ReservationStatus.OPENED, new ClientData(), new Date());
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        Product product = new Product(Id.generate(), new Money(new BigDecimal(5)), "paluszki", ProductType.FOOD);
        when(productRepository.load(any(Id.class))).thenReturn(product);

        productHandler.handle(productCommand);
        verify(reservationRepository, times(1)).load(any());
        verify(productRepository, times(1)).load(any());
        verify(reservationRepository, times(1)).save(any());
        assertThat(true, is(equalTo(true)));
    }

    @Test
    public void testIfProductIsActiveClientWillNotBeCalled() {
        Reservation reservation = new Reservation(Id.generate(), ReservationStatus.OPENED, new ClientData(), new Date());
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        Product product = new Product(Id.generate(), new Money(new BigDecimal(5)), "paluszki", ProductType.FOOD);
        when(productRepository.load(any(Id.class))).thenReturn(product);

        when(clientRepository.load(any(Id.class))).thenReturn(client);

        productHandler.handle(productCommand);

        verify(clientRepository, never()).load(any());
    }

    @Test
    public void testIfProductIsArchiveClientWillBeCalled() {
        Reservation reservation = new Reservation(Id.generate(), ReservationStatus.OPENED, new ClientData(), new Date());
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        Product product = new Product(Id.generate(), new Money(new BigDecimal(5)), "paluszki", ProductType.FOOD);
        product.markAsRemoved();
        when(productRepository.load(any(Id.class))).thenReturn(product);

        when(clientRepository.load(any(Id.class))).thenReturn(client);

        Product product2 = new Product(Id.generate(), new Money(new BigDecimal(50)), "pepsi", ProductType.STANDARD);
        when(suggestionService.suggestEquivalent(any(Product.class), any(Client.class))).thenReturn(product2);

        productHandler.handle(productCommand);

        verify(clientRepository, times(1)).load(any());
    }

}
