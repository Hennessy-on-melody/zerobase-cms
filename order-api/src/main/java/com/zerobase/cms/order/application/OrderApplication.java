package com.zerobase.cms.order.application;
import com.zerobase.cms.order.client.MailgunClient;
import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.client.mailgun.SendMailForm;
import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomerDto;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.service.ProductItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderApplication {
    private final CartApplication cartApplication;
    private final MailgunClient mailgunClient;
    private final UserClient userClient;
    private final ProductItemService productItemService;

    @Transactional
    public void order(String token, Cart cart) {
        Cart orderCart = cartApplication.refreshCart(cart);
        if (orderCart.getMessages().size() > 0) {
            throw new CustomException(ErrorCode.ORDER_FAIL_CHECK_CART);
        }
        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();

        int totalPrice = getTotalPrice(cart);
        if (customerDto.getBalance() < totalPrice) {
            throw new CustomException(ErrorCode.ORDER_FAIL_NO_MONEY);
        }

        userClient.changeBalance(token,
                ChangeBalanceForm.builder()
                        .from("USER")
                        .message("Order")
                        .money(-totalPrice)
                        .build());

        for (Cart.Product product : orderCart.getProducts()) {
            for (Cart.ProductItem cartItem : product.getItems()) {
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount() - cartItem.getCount());
            }
        }
        orderEmailSend(customerDto);
    }

    public void orderEmailSend(CustomerDto customerDto) {

        SendMailForm sendMailForm = SendMailForm.builder()
                .from("zerobase-test@from.com")
                .to(customerDto.getEmail())
                .subject("Order Confirmation Email")
                .text("주문을 완료하였습니다.")
                .build();
        log.info("Send email result : " + mailgunClient.sendEmail(sendMailForm).getBody());

    }


    public Integer getTotalPrice(Cart cart) {

        return cart.getProducts().stream().flatMapToInt
                        (product -> product.getItems().stream()
                                .flatMapToInt(productItem -> IntStream
                                        .of(productItem.getPrice()
                                                * productItem.getCount()))).sum();
    }


}