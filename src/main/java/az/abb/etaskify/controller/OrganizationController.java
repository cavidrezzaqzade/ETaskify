//package az.abb.etaskify.controller;
//
//import az.abb.etaskify.domain.RefreshJwtRequest;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.validation.Valid;
//
///**
// * @author caci
// * @since 19.05.2022
// */
//
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
//@Tag(name = "Organization", description = "the Organization API")
//public class OrganizationController {
//
//    @Operation(summary = "add organization", description = "add organization and default admin user", tags = {"Org"})
//    @PostMapping("orgs")
//    public ResponseEntity<?> addOrganization(@Valid @RequestBody RefreshJwtRequest request) {
////        return authService.getAccessToken(request.getRefreshToken());
//        return null;
//    }
//
//}
