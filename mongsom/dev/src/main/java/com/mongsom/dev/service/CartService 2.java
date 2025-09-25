package com.mongsom.dev.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.cart.reqDto.CartAddReqDto;
import com.mongsom.dev.dto.cart.reqDto.CartAllCheckReqDto;
import com.mongsom.dev.dto.cart.reqDto.CartChangeCheckReqDto;
import com.mongsom.dev.dto.cart.reqDto.CartUpdateQuantityReqDto;
import com.mongsom.dev.dto.cart.respDto.CartRespDto;
import com.mongsom.dev.entity.Cart;
import com.mongsom.dev.entity.Product;
import com.mongsom.dev.entity.ProductImg;
import com.mongsom.dev.entity.ProductOption;
import com.mongsom.dev.entity.User;
import com.mongsom.dev.repository.CartRepository;
import com.mongsom.dev.repository.ProductImgRepository;
import com.mongsom.dev.repository.ProductOptionRepository;
import com.mongsom.dev.repository.ProductRepository;
import com.mongsom.dev.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    
    private final CartRepository cartRepository;
    private final ProductImgRepository productImgRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    
    //장바구니조회
    @Transactional(readOnly = true)
    public RespDto<CartRespDto> getCartItems(Integer userCode) {
        try {
            // 사용자의 장바구니 조회 (상품, 옵션 정보 포함)
            List<Cart> cartItems = cartRepository.findByUserCodeWithProductAndOption(userCode);
            
            if (cartItems.isEmpty()) {
                return RespDto.<CartRespDto>builder()
                        .code(1)
                        .data(CartRespDto.from(List.of()))
                        .build();
            }
            
            // 상품 ID 목록 추출
            List<Integer> productIds = cartItems.stream()
                    .map(cart -> cart.getProduct().getProductId())
                    .distinct()
                    .collect(Collectors.toList());
            
            // 상품 이미지들 배치 조회 (N+1 문제 해결)
            List<ProductImg> productImgs = productImgRepository.findByProductIdInOrderByProductIdAndCreatedAt(productIds);
            
            // 상품 ID별로 이미지 URL 그룹화
            Map<Integer, List<String>> productImgMap = productImgs.stream()
                    .collect(Collectors.groupingBy(
                            img -> img.getProduct().getProductId(),
                            Collectors.mapping(ProductImg::getProductImgUrl, Collectors.toList())
                    ));
            
            // CartItemDto 리스트 생성
            List<CartRespDto.CartItemDto> cartItemDtos = cartItems.stream()
                    .map(cart -> {
                        List<String> productImgUrls = productImgMap.getOrDefault(
                                cart.getProduct().getProductId(), 
                                List.of()
                        );
                        return CartRespDto.CartItemDto.from(cart, productImgUrls);
                    })
                    .collect(Collectors.toList());
            
            CartRespDto cartRespDto = CartRespDto.from(cartItemDtos);
            
            //장바구니 조회 성공
            return RespDto.<CartRespDto>builder()
                    .code(1)
                    .data(cartRespDto)
                    .build();
                    
        } catch (Exception e) {
            //장바구니 조회 실패
            return RespDto.<CartRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    //장바구니 추가
    @Transactional
    public RespDto<String> addToCart(CartAddReqDto reqDto) {
        try {
            // 1. 사용자 존재 확인
            Optional<User> userOpt = userRepository.findUserByUserCode(reqDto.getUserCode());
            if (userOpt.isEmpty()) {
                return RespDto.<String>builder()
                        .code(-1)
                        .data("존재하지 않는 사용자입니다")
                        .build();
            }
            
            // 2. 상품 존재 확인
            Optional<Product> productOpt = productRepository.findById(reqDto.getProductId());
            if (productOpt.isEmpty()) {
                return RespDto.<String>builder()
                        .code(-1)
                        .data("존재하지 않는 상품입니다")
                        .build();
            }
            
            // 3. 옵션 존재 확인 (optId가 있는 경우)
            if (reqDto.getOptId() != null) {
                Optional<ProductOption> optionOpt = productOptionRepository.findById(reqDto.getOptId());
                if (optionOpt.isEmpty()) {
                    return RespDto.<String>builder()
                            .code(-1)
                            .data("존재하지 않는 상품 옵션입니다")
                            .build();
                }
            }
            
            // 4. 기존 장바구니 아이템 확인 (같은 상품+옵션 조합)
            List<Cart> existingCarts;
            if (reqDto.getOptId() != null) {
                existingCarts = cartRepository.findByUserCodeAndProductIdAndOptId(
                        reqDto.getUserCode(), reqDto.getProductId(), reqDto.getOptId());
            } else {
                existingCarts = cartRepository.findByUserCodeAndProductIdAndOptIdIsNull(
                        reqDto.getUserCode(), reqDto.getProductId());
            }
            
            if (!existingCarts.isEmpty()) {
                // 5. 기존 아이템이 있으면 수량 업데이트
                Cart existingCart = existingCarts.get(0);
                existingCart.setQuantity(existingCart.getQuantity() + reqDto.getQuantity());
                existingCart.setCheckStatus(reqDto.getCheckStatus());
                cartRepository.save(existingCart);
                
                return RespDto.<String>builder()
                        .code(1)
                        .data("장바구니에 추가되었습니다 (수량 업데이트)")
                        .build();
            } else {
                // 6. 새로운 장바구니 아이템 생성
                Cart newCart = Cart.builder()
                        .userCode(reqDto.getUserCode())
                        .productId(reqDto.getProductId())
                        .optId(reqDto.getOptId())
                        .quantity(reqDto.getQuantity())
                        .checkStatus(reqDto.getCheckStatus())
                        .build();
                
                Cart savedCart = cartRepository.save(newCart);
                
                return RespDto.<String>builder()
                        .code(1)
                        .data("장바구니에 추가되었습니다")
                        .build();
            }
            
        } catch (Exception e) {
            return RespDto.<String>builder()
                    .code(-1)
                    .data("장바구니 추가 중 오류가 발생했습니다")
                    .build();
        }
    }
    //장바구니상품삭제
    @Transactional
    public RespDto<String> deleteCartItem(Integer userCode, Integer productId, Integer optId) {
        try {
            // 1. 사용자 존재 확인
            Optional<User> userOpt = userRepository.findUserByUserCode(userCode);
            if (userOpt.isEmpty()) {
                return RespDto.<String>builder()
                        .code(-1)
                        .data("존재하지 않는 사용자입니다")
                        .build();
            }
            
            // 2. 해당 사용자의 장바구니에서 상품 존재 확인
            List<Cart> cartItems = cartRepository.findByUserCodeAndProductId(userCode, productId);
            
            if (cartItems.isEmpty()) {
                return RespDto.<String>builder()
                        .code(-1)
                        .data("장바구니에 해당 상품이 없습니다")
                        .build();
            }
            
            // 3. 해당 상품의 모든 옵션 삭제 (옵션이 여러 개 있을 수 있음)
            int deletedCount = cartRepository.deleteByUserCodeAndProductIdAndOptId(userCode, productId, optId);
            
            if (deletedCount > 0) {
                log.info("장바구니 상품 삭제 완료 - userCode: {}, productId: {}, optId: {}, 삭제된 아이템 수: {}", 
                        userCode, productId, optId, deletedCount);
                
                return RespDto.<String>builder()
                        .code(1)
                        .data("장바구니에서 상품이 삭제되었습니다")
                        .build();
            } else {
                log.error("장바구니 상품 삭제 실패 - userCode: {}, productId: {}, optId: {}", userCode, productId, optId);
                return RespDto.<String>builder()
                        .code(-1)
                        .data("장바구니 상품 삭제에 실패했습니다")
                        .build();
            }
                    
        } catch (Exception e) {
            return RespDto.<String>builder()
                    .code(-1)
                    .data("장바구니 상품 삭제 중 오류가 발생했습니다")
                    .build();
        }
    }
    //장바구니수량변경
    @Transactional
    public RespDto<String> updateCartQuantity(CartUpdateQuantityReqDto reqDto) {
        try {
            // 1. 사용자 존재 확인
            Optional<User> userOpt = userRepository.findUserByUserCode(reqDto.getUserCode());
            if (userOpt.isEmpty()) {
                return RespDto.<String>builder()
                        .code(-1)
                        .data("존재하지 않는 사용자입니다")
                        .build();
            }
            
            // 2. 장바구니 아이템 조회 (productId + optId 조합으로)
            List<Cart> cartItems;
            if (reqDto.getOptId() != null) {
                cartItems = cartRepository.findByUserCodeAndProductIdAndOptId(
                        reqDto.getUserCode(), reqDto.getProductId(), reqDto.getOptId());
            } else {
                cartItems = cartRepository.findByUserCodeAndProductIdAndOptIdIsNull(
                        reqDto.getUserCode(), reqDto.getProductId());
            }
            
            if (cartItems.isEmpty()) {
                return RespDto.<String>builder()
                        .code(-1)
                        .data("해당 장바구니 아이템을 찾을 수 없습니다")
                        .build();
            }
            
            // 3. 수량 업데이트 (첫 번째 아이템만 업데이트 - 동일한 조합은 하나만 있어야 함)
            Cart cart = cartItems.get(0);
            Integer oldQuantity = cart.getQuantity();
            cart.setQuantity(reqDto.getQuantity());
            cartRepository.save(cart);
            
            return RespDto.<String>builder()
                    .code(1)
                    .data("수량이 변경되었습니다")
                    .build();
                    
        } catch (Exception e) {
            return RespDto.<String>builder()
                    .code(-1)
                    .data("수량 변경 중 오류가 발생했습니다")
                    .build();
        }
    }
    //장바구니체크변경
    @Transactional
    public RespDto<String> changeCheckStatus(CartChangeCheckReqDto reqDto) {
        try {
            // 1. 사용자 존재 확인
            Optional<User> userOpt = userRepository.findUserByUserCode(reqDto.getUserCode());
            if (userOpt.isEmpty()) {
                return RespDto.<String>builder()
                        .code(-1)
                        .data("존재하지 않는 사용자입니다")
                        .build();
            }
            
            // 2. 장바구니 아이템 조회 (productId + optId 조합으로)
            List<Cart> cartItems;
            if (reqDto.getOptId() != null) {
                cartItems = cartRepository.findByUserCodeAndProductIdAndOptId(
                        reqDto.getUserCode(), reqDto.getProductId(), reqDto.getOptId());
            } else {
                cartItems = cartRepository.findByUserCodeAndProductIdAndOptIdIsNull(
                        reqDto.getUserCode(), reqDto.getProductId());
            }
            
            if (cartItems.isEmpty()) {
                return RespDto.<String>builder()
                        .code(-1)
                        .data("해당 장바구니 아이템을 찾을 수 없습니다")
                        .build();
            }
            
            // 3. 체크 상태 토글 (현재 상태의 반대로 변경)
            Cart cart = cartItems.get(0);
            Integer currentStatus = cart.getCheckStatus();
            Integer newStatus = 0 ;
            if(currentStatus == 0) {
            	newStatus = 1;
            } else {
            	newStatus = 0;
            }
            
            cart.setCheckStatus(newStatus);
            cartRepository.save(cart);
            
            String statusMessage = newStatus == 1 ? "체크됨" : "체크 해제됨";
            
            return RespDto.<String>builder()
                    .code(1)
                    .data("체크 상태가 변경되었습니다 (" + statusMessage + ")")
                    .build();
                    
        } catch (Exception e) {
            return RespDto.<String>builder()
                    .code(-1)
                    .data("체크 상태 변경 중 오류가 발생했습니다")
                    .build();
        }
    }
    
    // 장바구니 전체 체크 상태 변경
    @Transactional
    public RespDto<String> updateAllCheckStatus(CartAllCheckReqDto reqDto) {
        try {
            log.info("=== 장바구니 전체 체크 상태 변경 시작 - userCode: {}, allCheckStatus: {} ===", 
                    reqDto.getUserCode(), reqDto.getAllCheckStatus());
            
            // 1. allCheckStatus 값 검증
            if (reqDto.getAllCheckStatus() != 0 && reqDto.getAllCheckStatus() != 1) {
                log.warn("잘못된 체크 상태 값 - userCode: {}, allCheckStatus: {}", 
                        reqDto.getUserCode(), reqDto.getAllCheckStatus());
                return RespDto.<String>builder()
                        .code(-1)
                        .data("체크 상태는 0(해제) 또는 1(선택)이어야 합니다")
                        .build();
            }
            
            // 2. 사용자 존재 확인
            Optional<User> userOpt = userRepository.findUserByUserCode(reqDto.getUserCode());
            if (userOpt.isEmpty()) {
                log.warn("존재하지 않는 사용자 - userCode: {}", reqDto.getUserCode());
                return RespDto.<String>builder()
                        .code(-1)
                        .data("존재하지 않는 사용자입니다")
                        .build();
            }
            
            // 3. 해당 사용자의 장바구니 존재 확인
            List<Cart> cartItems = cartRepository.findByUserCodeOrderByCreatedAtDesc(reqDto.getUserCode());
            if (cartItems.isEmpty()) {
                log.warn("장바구니가 비어있습니다 - userCode: {}", reqDto.getUserCode());
                return RespDto.<String>builder()
                        .code(-1)
                        .data("장바구니가 비어있습니다")
                        .build();
            }
            
            // 4. 전체 체크 상태 업데이트 (Integer 값으로 직접 전달)
            int updatedCount = cartRepository.updateAllCheckStatusByUserCode(
                    reqDto.getUserCode(), reqDto.getAllCheckStatus());
            
            if (updatedCount > 0) {
                String statusMessage = (reqDto.getAllCheckStatus() == 1) ? "전체 선택" : "전체 해제";
                log.info("장바구니 전체 체크 상태 변경 완료 - userCode: {}, 변경된 아이템 수: {}, 상태: {}", 
                        reqDto.getUserCode(), updatedCount, statusMessage);
                
                return RespDto.<String>builder()
                        .code(1)
                        .data("장바구니 " + statusMessage + "이 완료되었습니다")
                        .build();
            } else {
                log.error("장바구니 전체 체크 상태 변경 실패 - userCode: {}", reqDto.getUserCode());
                return RespDto.<String>builder()
                        .code(-1)
                        .data("장바구니 전체 체크 상태 변경에 실패했습니다")
                        .build();
            }
            
        } catch (Exception e) {
            log.error("장바구니 전체 체크 상태 변경 실패 - userCode: {}, allCheckStatus: {}, error: {}", 
                    reqDto.getUserCode(), reqDto.getAllCheckStatus(), e.getMessage());
            return RespDto.<String>builder()
                    .code(-1)
                    .data("장바구니 전체 체크 상태 변경 중 오류가 발생했습니다")
                    .build();
        }
    }
    
}