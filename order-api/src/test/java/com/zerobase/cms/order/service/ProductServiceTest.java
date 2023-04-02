package com.zerobase.cms.order.service;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;



@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
        public void addProductTest() throws Exception{
        //given
        Long sellerId = 1L;
        AddProductForm form = makeProductForm("나이키 에어포스", "신발", 3);

        //when
        Product p = productService.addProduct(sellerId, form);
        //then
        given(productRepository.save(any()))
                .willReturn(Product.of(sellerId, form));

        // 나머지 필드들에 대한 검증(숙제)
//        assertEquals("나이키 에어포스", p.getName());
//        assertEquals("신발", p.getDescription());
//        assertEquals(3, p.getProductItems().size());
//        assertEquals(1L, p.getProductItems().get(0).getId().longValue());
//        assertEquals("나이키 에어포스", p.getProductItems().get(0).getName());
//        assertEquals(10000, p.getProductItems().get(0).getPrices());

//        assertEquals("나이키 에어포스", result.getName());
//        assertEquals("신발", result.getDescription());
//        assertEquals(3, result.getProductItems().size());


     }
    private static AddProductForm makeProductForm(String name, String description, int itemCount) {
        List<AddProductItemForm> addProductItemForms = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            addProductItemForms.add(makeProductItemForm(1L, name + i));
        }
        return AddProductForm.builder()
                .name(name)
                .description(description)
                .items(addProductItemForms)
                .build();
    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(10000)
                .count(1)
                .build();
    }
}