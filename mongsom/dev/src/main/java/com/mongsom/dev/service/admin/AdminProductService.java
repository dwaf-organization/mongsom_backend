package com.mongsom.dev.service.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongsom.dev.common.dto.PaginationDto;
import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.product.reqDto.AdminProductUpdateReqDto;
import com.mongsom.dev.dto.admin.product.reqDto.ChangeApprovalReqDto;
import com.mongsom.dev.dto.admin.product.reqDto.ProductRegistReqDto;
import com.mongsom.dev.dto.admin.product.respDto.AdminProductDetailRespDto;
import com.mongsom.dev.dto.admin.product.respDto.AdminProductListRespDto;
import com.mongsom.dev.dto.admin.product.respDto.AdminProductSelectRespDto;
import com.mongsom.dev.dto.admin.product.respDto.ChangeProductListRespDto;
import com.mongsom.dev.entity.ChangeItem;
import com.mongsom.dev.entity.OrderDetail;
import com.mongsom.dev.entity.OrderItem;
import com.mongsom.dev.entity.Product;
import com.mongsom.dev.entity.ProductImg;
import com.mongsom.dev.entity.ProductOption;
import com.mongsom.dev.repository.CartRepository;
import com.mongsom.dev.repository.ChangeItemRepository;
import com.mongsom.dev.repository.OrderDetailRepository;
import com.mongsom.dev.repository.OrderItemRepository;
import com.mongsom.dev.repository.ProductImgRepository;
import com.mongsom.dev.repository.ProductOptionRepository;
import com.mongsom.dev.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProductService {
    
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductImgRepository productImgRepository;
    private final ChangeItemRepository changeItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartRepository cartRepository;
    
    //상품등록
    @Transactional
    public RespDto<Boolean> registProduct(ProductRegistReqDto reqDto) {
        try {
            log.info("상품 등록 시작 - name: {}", reqDto.getName());

            // 상품명 중복 체크
            if (productRepository.existsByName(reqDto.getName())) {
                log.warn("이미 존재하는 상품명 - name: {}", reqDto.getName());
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }

            // 1단계: Product 엔티티 생성 (옵션/이미지 제외)
            Product product = Product.builder()
                    .name(reqDto.getName())
                    .contents(reqDto.getContents())
                    .premium(reqDto.getPremium())
                    .price(reqDto.getPrice())
                    .salesMargin(reqDto.getSalesMargin())
                    .discountPer(reqDto.getDiscountPer())
                    .discountPrice(reqDto.getDiscountPrice())
                    .deliveryPrice(reqDto.getDeliveryPrice())
                    .build();

            // 2단계: Product 먼저 저장해서 productId 생성
            Product savedProduct = productRepository.save(product);
            log.info("Product 저장 완료 - productId: {}", savedProduct.getProductId());

            // 3단계: 이제 productId가 있으니 옵션 추가
            for (String optName : reqDto.getOptNames()) {
                if (optName != null && !optName.trim().isEmpty()) {
                    ProductOption productOption = new ProductOption(optName.trim());
                    savedProduct.addProductOption(productOption);
                }
            }

            // 4단계: 상품 이미지 추가
            for (String imgUrl : reqDto.getProductImgUrls()) {
                if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                    ProductImg productImg = new ProductImg(imgUrl.trim());
                    savedProduct.addProductImage(productImg);
                }
            }

            // 유효성 검증
            if (savedProduct.getProductOptions().isEmpty()) {
                log.warn("상품 옵션이 없음 - name: {}", reqDto.getName());
                return RespDto.<Boolean>builder()
                        .code(-3)
                        .data(false)
                        .build();
            }

            if (savedProduct.getProductImages().isEmpty()) {
                log.warn("상품 이미지가 없음 - name: {}", reqDto.getName());
                return RespDto.<Boolean>builder()
                        .code(-4)
                        .data(false)
                        .build();
            }

            // 5단계: 옵션과 이미지까지 포함해서 다시 저장
            Product finalProduct = productRepository.save(savedProduct);

            log.info("상품 등록 완료 - productId: {}, name: {}, optionCount: {}, imageCount: {}",
                    finalProduct.getProductId(), finalProduct.getName(),
                    finalProduct.getProductOptions().size(), finalProduct.getProductImages().size());

            return RespDto.<Boolean>builder()
                    .code(1)
                    .data(true)
                    .build();

        } catch (Exception e) {
            log.error("상품 등록 실패 - name: {}", reqDto.getName(), e);
            return RespDto.<Boolean>builder()
                    .code(-1)
                    .data(false)
                    .build();
        }
    }
    
    //상품조회
    @Transactional(readOnly = true)
    public RespDto<AdminProductListRespDto> selectProducts(Integer page, Integer size, 
                                                          String name, Integer premium) {
        try {
            log.info("=== 관리자 상품 조회 시작 - page: {}, size: {}, name: {}, premium: {} ===", 
                    page, size, name, premium);
            
            // Pageable 생성 (최신순 정렬)
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            
            // 조건에 따른 상품 조회
            Page<Product> productPage = getProductsByCondition(name, premium, pageable);
            
            // DTO 변환
            List<AdminProductSelectRespDto> productDtos = productPage.getContent().stream()
                    .map(this::convertToAdminProductDto)
                    .collect(Collectors.toList());
            
            // 페이징 정보 생성
            PaginationDto pagination = PaginationDto.builder()
                    .currentPage(page + 1)  // 0-based를 1-based로 변환
                    .totalPage(productPage.getTotalPages())
                    .size(productDtos.size())
                    .hasNext(productPage.hasNext())
                    .build();
            
            // 최종 응답 DTO 생성
            AdminProductListRespDto result = AdminProductListRespDto.builder()
                    .products(productDtos)
                    .pagination(pagination)
                    .build();
            
            log.info("=== 관리자 상품 조회 완료 - 조회된 상품 수: {}, 전체 페이지: {} ===", 
                    productDtos.size(), productPage.getTotalPages());
            
            return RespDto.<AdminProductListRespDto>builder()
                    .code(1)
                    .data(result)
                    .build();
                    
        } catch (Exception e) {
            log.error("관리자 상품 조회 실패 - name: {}, premium: {}, error: {}", 
                    name, premium, e.getMessage(), e);
            return RespDto.<AdminProductListRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    // 조건에 따른 상품 조회
    private Page<Product> getProductsByCondition(String name, Integer premium, Pageable pageable) {
        if (name != null && !name.trim().isEmpty() && premium != null && premium != 2) {
            // 상품명 + 프리미엄 조건 모두 적용
            return productRepository.findByNameContainingAndPremiumAndDeleteStatus(name.trim(), premium, 0, pageable);
        } else if (name != null && !name.trim().isEmpty()) {
            // 상품명만 조건 적용
            return productRepository.findByNameContainingAndDeleteStatus(name.trim(), 0, pageable);
        } else if (premium != null && premium != 2) {
            // 프리미엄 조건만 적용
            return productRepository.findByPremiumAndDeleteStatus(premium, 0, pageable);
        } else {
            // 전체 조회 (삭제되지 않은 상품만)
            return productRepository.findByDeleteStatus(0, pageable);
        }
    }
    
    // Product 엔티티를 AdminProductSelectRespDto로 변환
    private AdminProductSelectRespDto convertToAdminProductDto(Product product) {
        try {
            // 상품 옵션 조회
            List<AdminProductSelectRespDto.ProductOptionDto> options = 
                    productOptionRepository.findByProductId(product.getProductId())
                    .stream()
                    .map(option -> AdminProductSelectRespDto.ProductOptionDto.builder()
                            .optId(option.getOptId())
                            .optName(option.getOptName())
                            .build())
                    .collect(Collectors.toList());
            
            // 상품 이미지 URL 조회
            List<String> imageUrls = productImgRepository.findImgUrlsByProductId(product.getProductId());
            
            return AdminProductSelectRespDto.builder()
                    .productId(product.getProductId())
                    .name(product.getName())
                    .premium(product.getPremium())
                    .price(product.getPrice())
                    .salesMargin(product.getSalesMargin())
                    .discountPer(product.getDiscountPer())
                    .discountPrice(product.getDiscountPrice())
                    .deliveryPrice(product.getDeliveryPrice())
                    .options(options)
                    .imageUrls(imageUrls)
                    .build();
                    
        } catch (Exception e) {
            log.error("상품 DTO 변환 실패 - productId: {}, error: {}", 
                    product.getProductId(), e.getMessage());
            // 오류 발생 시 기본 정보만 리턴
            return AdminProductSelectRespDto.builder()
                    .productId(product.getProductId())
                    .name(product.getName())
                    .premium(product.getPremium())
                    .price(product.getPrice())
                    .salesMargin(product.getSalesMargin())
                    .discountPer(product.getDiscountPer())
                    .discountPrice(product.getDiscountPrice())
                    .deliveryPrice(product.getDeliveryPrice())
                    .options(List.of())
                    .imageUrls(List.of())
                    .build();
        }
    }
    
    // 관리자 상품 상세조회
    @Transactional(readOnly = true)
    public RespDto<AdminProductDetailRespDto> selectProductDetail(Integer productId) {
        try {
            log.info("=== 관리자 상품 상세조회 시작 - productId: {} ===", productId);
            
            // 1. 상품 기본 정보 조회
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                log.warn("상품을 찾을 수 없음 - productId: {}", productId);
                return RespDto.<AdminProductDetailRespDto>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
            // 2. 상품 옵션 리스트 조회 (optId + optName)
            List<AdminProductDetailRespDto.ProductOptionDto> options = 
                    productOptionRepository.findByProductId(productId)
                    .stream()
                    .map(option -> AdminProductDetailRespDto.ProductOptionDto.builder()
                            .optId(option.getOptId())
                            .optName(option.getOptName())
                            .build())
                    .collect(Collectors.toList());
            
            // 3. 상품 이미지 URL 리스트 조회
            List<String> productImgUrls = productImgRepository.findImgUrlsByProductId(productId);
            
            // 4. DTO 생성
            AdminProductDetailRespDto result = AdminProductDetailRespDto.builder()
                    .productId(product.getProductId())
                    .name(product.getName())
                    .contents(product.getContents())
                    .premium(product.getPremium())
                    .price(product.getPrice())
                    .salesMargin(product.getSalesMargin())
                    .discountPer(product.getDiscountPer())
                    .discountPrice(product.getDiscountPrice())
                    .deliveryPrice(product.getDeliveryPrice())
                    .deleteStatus(product.getDeleteStatus())
                    .options(options)
                    .productImgUrls(productImgUrls)
                    .build();
            
            log.info("=== 관리자 상품 상세조회 완료 - productId: {}, optionCount: {}, imageCount: {} ===", 
                    productId, options.size(), productImgUrls.size());
            
            return RespDto.<AdminProductDetailRespDto>builder()
                    .code(1)
                    .data(result)
                    .build();
            
        } catch (Exception e) {
            log.error("관리자 상품 상세조회 실패 - productId: {}, error: {}", 
                    productId, e.getMessage(), e);
            return RespDto.<AdminProductDetailRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
   
    // 관리자 상품 수정
    @Transactional
    public RespDto<Boolean> updateProduct(Integer productId, AdminProductUpdateReqDto reqDto) {
        try {
            log.info("=== 관리자 상품 수정 시작 - productId: {}, name: {} ===", 
                    productId, reqDto.getName());
            
            // 1. 상품 존재 여부 확인
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                log.warn("상품을 찾을 수 없음 - productId: {}", productId);
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            // 2. 상품 기본 정보 업데이트
            product.updateProduct(
                    reqDto.getName(),
                    reqDto.getContents(),
                    reqDto.getPremium(),
                    reqDto.getPrice(),
                    reqDto.getSalesMargin(),
                    reqDto.getDiscountPer(),
                    reqDto.getDiscountPrice(),
                    reqDto.getDeliveryPrice()
            );
            
            // 3. 옵션 업데이트 (optId 기준)
            updateProductOptions(productId, reqDto.getOptions());
            
            // 4. 이미지 전체 삭제 후 재생성 (방법 1)
            updateProductImages(product, reqDto.getProductImgUrls());
            
            // 5. 상품 저장
            productRepository.save(product);
            
            log.info("=== 관리자 상품 수정 완료 - productId: {}, optionCount: {}, imageCount: {} ===", 
                    productId, reqDto.getOptions().size(), reqDto.getProductImgUrls().size());
            
            return RespDto.<Boolean>builder()
                    .code(1)
                    .data(true)
                    .build();
                    
        } catch (Exception e) {
            log.error("관리자 상품 수정 실패 - productId: {}, error: {}", 
                    productId, e.getMessage(), e);
            return RespDto.<Boolean>builder()
                    .code(-1)
                    .data(false)
                    .build();
        }
    }
    
    // 옵션 업데이트 (수정 + 생성만)
    private void updateProductOptions(Integer productId, List<AdminProductUpdateReqDto.ProductOptionDto> newOptions) {
        try {
            log.info("옵션 업데이트 시작 - productId: {}", productId);
            
            // 옵션 처리 (수정 또는 새로 생성만)
            for (AdminProductUpdateReqDto.ProductOptionDto optionDto : newOptions) {
                if (optionDto.getOptId() == null) {
                    // 새로 생성
                    ProductOption newOption = ProductOption.builder()
                            .optName(optionDto.getOptName())
                            .productId(productId)
                            .build();
                    productOptionRepository.save(newOption);
                    log.info("새 옵션 생성 완료 - optName: {}", optionDto.getOptName());
                } else {
                    // 기존 옵션 수정
                    ProductOption existingOption = productOptionRepository.findById(optionDto.getOptId()).orElse(null);
                    if (existingOption != null) {
                        existingOption.updateOptionName(optionDto.getOptName());
                        productOptionRepository.save(existingOption);
                        log.info("옵션 수정 완료 - optId: {}, optName: {}", 
                                optionDto.getOptId(), optionDto.getOptName());
                    } else {
                        log.warn("수정할 옵션을 찾을 수 없음 - optId: {}", optionDto.getOptId());
                    }
                }
            }
            
            log.info("옵션 업데이트 완료 - 처리된 옵션 수: {}", newOptions.size());
            
        } catch (Exception e) {
            log.error("옵션 업데이트 실패 - productId: {}, error: {}", productId, e.getMessage());
            throw e;
        }
    }
    
    // 이미지 전체 삭제 후 재생성 (방법 1)
    private void updateProductImages(Product product, List<String> newImageUrls) {
        try {
            log.info("이미지 전체 업데이트 시작 - productId: {}", product.getProductId());
            
            // 1. 기존 이미지 모두 삭제
            productImgRepository.deleteByProductId(product.getProductId());
            log.info("기존 이미지 모두 삭제 완료");
            
            // 2. 새로운 이미지들 모두 생성
            for (String imgUrl : newImageUrls) {
                if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                    ProductImg productImg = new ProductImg(imgUrl.trim());
                    product.addProductImage(productImg);
                }
            }
            
            log.info("새로운 이미지 생성 완료 - 생성된 이미지 수: {}", newImageUrls.size());
            
        } catch (Exception e) {
            log.error("이미지 업데이트 실패 - productId: {}, error: {}", 
                    product.getProductId(), e.getMessage());
            throw e;
        }
    }
    
    // 관리자 상품 삭제
    @Transactional
    public RespDto<Boolean> softDeleteProduct(Integer productId) {
        try {
            log.info("=== 관리자 상품 소프트 삭제 시작 - productId: {} ===", productId);
            
            // 1. 상품 존재 여부 확인
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                log.warn("삭제할 상품을 찾을 수 없음 - productId: {}", productId);
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            // 2. 이미 삭제된 상품인지 확인
            if (product.isDeleted()) {
                log.warn("이미 삭제된 상품 - productId: {}", productId);
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            // 3. 상품 소프트 삭제 (delete_status = 1)
            product.softDelete();
            productRepository.save(product);
            log.info("상품 소프트 삭제 완료 - productId: {}", productId);
            
            // 4. 해당 상품의 모든 장바구니 항목 삭제
            int deletedCartItems = cartRepository.deleteByProductId(productId);
            log.info("장바구니에서 상품 삭제 완료 - productId: {}, 삭제된 항목 수: {}", 
                    productId, deletedCartItems);
            
            log.info("=== 관리자 상품 소프트 삭제 완료 - productId: {} ===", productId);
            
            return RespDto.<Boolean>builder()
                    .code(1)
                    .data(true)
                    .build();
                    
        } catch (Exception e) {
            log.error("관리자 상품 소프트 삭제 실패 - productId: {}, error: {}", 
                    productId, e.getMessage(), e);
            return RespDto.<Boolean>builder()
                    .code(-1)
                    .data(false)
                    .build();
        }
    }
    
    // 교환/반품 상품 목록 조회
    public RespDto<ChangeProductListRespDto> getChangeProductList(Integer changeStatus, Integer page) {
        try {
            log.info("=== 교환/반품 상품 목록 조회 시작 - changeStatus: {}, page: {} ===", changeStatus, page);
            
            // 1. 페이징 설정 (사이즈 5 고정)
            Pageable pageable = PageRequest.of(page - 1, 5);
            
            // 2. 교환/반품 항목 조회 (복합 조인 쿼리)
            List<Object[]> changeProductRows = changeItemRepository.findChangeProductListByStatus(changeStatus, pageable);
            
            // 3. DTO 변환
            List<ChangeProductListRespDto.ChangeItemDto> changeItemDtos = changeProductRows.stream()
                    .map(this::convertToChangeItemDto)
                    .collect(Collectors.toList());
            
            // 4. 전체 개수 조회 (페이징 정보용)
            long totalElements = changeItemRepository.countByChangeStatus(changeStatus);
            int totalPages = (int) Math.ceil((double) totalElements / 5);
            boolean hasNext = page < totalPages;
            
            // 5. 응답 DTO 생성
            ChangeProductListRespDto responseDto = ChangeProductListRespDto.builder()
                    .changeItems(changeItemDtos)
                    .pagination(ChangeProductListRespDto.PaginationDto.builder()
                            .currentPage(page)
                            .totalPage(totalPages)
                            .size(5)
                            .hasNext(hasNext)
                            .build())
                    .build();
            
            log.info("=== 교환/반품 상품 목록 조회 완료 - 조회된 항목 수: {}, 총 페이지: {} ===", 
                    changeItemDtos.size(), totalPages);
            
            return RespDto.<ChangeProductListRespDto>builder()
                    .code(1)
                    .data(responseDto)
                    .build();
                    
        } catch (Exception e) {
            log.error("교환/반품 상품 목록 조회 실패 - changeStatus: {}, page: {}, error: {}", 
                    changeStatus, page, e.getMessage(), e);
            return RespDto.<ChangeProductListRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    /**
     * Object[] 배열을 ChangeItemDto로 변환
     */
    private ChangeProductListRespDto.ChangeItemDto convertToChangeItemDto(Object[] row) {
        try {
            // 이미지 URL 문자열을 List로 변환
            String imageUrls = (String) row[12];  // GROUP_CONCAT 결과는 마지막 인덱스
            List<String> productImgUrls = parseImageUrls(imageUrls);
            
            // userCode Integer -> Long 변환
            Long userCode = ((Integer) row[3]).longValue();
            
            // Timestamp -> LocalDateTime 변환
            java.sql.Timestamp timestamp = (java.sql.Timestamp) row[7];
            java.time.LocalDateTime paymentAt = timestamp.toLocalDateTime();
            
            return ChangeProductListRespDto.ChangeItemDto.builder()
                    .changeId((Integer) row[0])           // ci.change_id
                    .orderDetailId((Integer) row[1])      // ci.order_detail_id
                    .orderId((Integer) row[2])            // ci.order_id
                    .userCode(userCode)                   // ci.user_code (Integer -> Long)
                    .changeStatus((Integer) row[4])       // ci.change_status
                    .approvalStatus((Integer) row[5])     // ci.approval_status
                    .contents((String) row[6])            // ci.contents
                    .paymentAt(paymentAt)                 // oi.payment_at (Timestamp -> LocalDateTime)
                    .receivedUserName((String) row[8])    // oi.received_user_name
                    .price((Integer) row[9])
                    .productName((String) row[10])        // p.name
                    .optName((String) row[11])
                    .productImgUrls(productImgUrls)       // GROUP_CONCAT(pi.img_url)
                    .build();
                    
        } catch (Exception e) {
            log.error("ChangeItemDto 변환 실패 - row 길이: {}, 오류: {}", row.length, e.getMessage());
            return null;
        }
    }
    
    // 이미지 URL 문자열을 List로 변환
    private List<String> parseImageUrls(String imageUrls) {
        if (imageUrls == null || imageUrls.trim().isEmpty()) {
            return List.of();
        }
        return List.of(imageUrls.split(","));
    }
    
    // 교환/반품 승인/반려 처리
    @Transactional
    public RespDto<Boolean> updateChangeApproval(ChangeApprovalReqDto reqDto) {
        try {
            log.info("=== 교환/반품 승인/반려 처리 시작 - changeId: {}, approvalStatus: {} ===", 
                    reqDto.getChangeId(), reqDto.getApprovalStatus());
            
            // 1. 교환/반품 항목 존재 여부 확인
            Optional<ChangeItem> changeItemOpt = changeItemRepository.findById(reqDto.getChangeId());
            if (changeItemOpt.isEmpty()) {
                log.warn("교환/반품 항목을 찾을 수 없음 - changeId: {}", reqDto.getChangeId());
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            ChangeItem changeItem = changeItemOpt.get();
            Integer changeStatus = changeItem.getChangeStatus();  // 1=교환, 2=반품
            Integer orderId = changeItem.getOrderId();
            Integer orderDetailId = changeItem.getOrderDetailId();
            
            // 2. approval_status 업데이트
            int updatedRows = changeItemRepository.updateApprovalStatus(
                    reqDto.getChangeId(), reqDto.getApprovalStatus());
            
            if (updatedRows > 0) {
                String statusText = getApprovalStatusText(reqDto.getApprovalStatus());
                log.info("=== 교환/반품 승인/반려 처리 완료 - changeId: {}, 상태: {} ===",
                        reqDto.getChangeId(), statusText);

                // 3. 반품(changeStatus=2)이고 승인(approvalStatus=1)인 경우 추가 처리
                if (changeStatus == 2 && reqDto.getApprovalStatus() == 1) {
                    log.info("=== 반품 승인 추가 처리 시작 - orderId: {}, orderDetailId: {} ===", 
                            orderId, orderDetailId);
                    
                    // 3-1. order_detail의 order_status를 1로 변경
                    Optional<OrderDetail> orderDetailOpt = orderDetailRepository.findById(orderDetailId);
                    if (orderDetailOpt.isPresent()) {
                        OrderDetail orderDetail = orderDetailOpt.get();
                        orderDetail.setOrderStatus(1);
                        orderDetailRepository.save(orderDetail);
                        
                        log.info("OrderDetail 업데이트 완료 - orderDetailId: {}, orderStatus: 1", orderDetailId);
                        
                        // 3-2. 같은 orderId를 가진 모든 OrderDetail 조회
                        List<OrderDetail> allOrderDetails = orderDetailRepository.findByOrderId(orderId);
                        
                        // 3-3. 모든 주문 상세의 orderStatus가 1인지 확인
                        boolean allCanceled = allOrderDetails.stream()
                                .allMatch(od -> od.getOrderStatus() != null && od.getOrderStatus() == 1);
                        
                        log.info("주문 상세 확인 - orderId: {}, 전체 상품 수: {}, 모두 취소: {}", 
                                orderId, allOrderDetails.size(), allCanceled);
                        
                        // 3-4. 모두 취소되었으면 order_item의 delivery_status를 '주문취소'로 변경
                        if (allCanceled) {
                            Optional<OrderItem> orderItemOpt = orderItemRepository.findById(orderId);
                            if (orderItemOpt.isPresent()) {
                                OrderItem orderItem = orderItemOpt.get();
                                orderItem.setDeliveryStatus("주문취소");
                                orderItemRepository.save(orderItem);
                                
                                log.info("OrderItem 업데이트 완료 - orderId: {}, deliveryStatus: 주문취소", orderId);
                            } else {
                                log.warn("OrderItem을 찾을 수 없음 - orderId: {}", orderId);
                            }
                        } else {
                            log.info("일부 상품만 취소됨 - OrderItem 상태 유지");
                        }
                    } else {
                        log.warn("OrderDetail을 찾을 수 없음 - orderDetailId: {}", orderDetailId);
                    }
                    
                    log.info("=== 반품 승인 추가 처리 완료 ===");
                }

                return RespDto.<Boolean>builder()
                        .code(1)
                        .data(true)
                        .build();
            } else {
                log.warn("교환/반품 승인/반려 처리 실패 - 업데이트된 행 없음, changeId: {}", reqDto.getChangeId());
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
                    
        } catch (Exception e) {
            log.error("교환/반품 승인/반려 처리 실패 - changeId: {}, approvalStatus: {}, error: {}", 
                    reqDto.getChangeId(), reqDto.getApprovalStatus(), e.getMessage(), e);
            return RespDto.<Boolean>builder()
                    .code(-1)
                    .data(false)
                    .build();
        }
    }

    private String getApprovalStatusText(Integer approvalStatus) {
        switch (approvalStatus) {
            case 0: return "승인대기";
            case 1: return "승인"; 
            case 2: return "반려";
            default: return "알 수 없음";
        }
    }
    
}