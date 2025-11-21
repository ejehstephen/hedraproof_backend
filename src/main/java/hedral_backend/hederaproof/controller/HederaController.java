package hedral_backend.hederaproof.controller;

import hedral_backend.hederaproof.model.MintRequest;
import hedral_backend.hederaproof.model.MintResponse;
import hedral_backend.hederaproof.model.VerifyRequest;
import hedral_backend.hederaproof.model.VerifyResponse;
import hedral_backend.hederaproof.model.TokenInfoResponse;
import hedral_backend.hederaproof.model.ReceiptRecord;
import hedral_backend.hederaproof.model.NftReceiptMetadataTemplate;
import hedral_backend.hederaproof.service.HederaService;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api")
public class HederaController {

    private final HederaService hederaService;

    public HederaController(HederaService hederaService) {
        this.hederaService = hederaService;
    }

    @PostMapping("/mint-receipt")
    public ResponseEntity<MintResponse> mintReceipt(@RequestBody MintRequest request) {
        try {
            MintResponse response = hederaService.mintNftReceipt(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TimeoutException | PrecheckStatusException | ReceiptStatusException e) {
            // Log the exception for debugging
            e.printStackTrace();
            MintResponse errorResponse = new MintResponse();
            errorResponse.setSuccess(false);
            // You might want to add a more specific error message here
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            MintResponse errorResponse = new MintResponse();
            errorResponse.setSuccess(false);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/receipts")
    public ResponseEntity<List<ReceiptRecord>> getReceipts() {
        List<ReceiptRecord> records = hederaService.getAllReceiptRecords();
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @PostMapping("/verify-receipt")
    public ResponseEntity<VerifyResponse> verifyReceipt(@RequestBody VerifyRequest request) {
        try {
            VerifyResponse response = hederaService.verifyNftReceipt(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TimeoutException | PrecheckStatusException | ReceiptStatusException e) {
            e.printStackTrace();
            VerifyResponse errorResponse = new VerifyResponse();
            errorResponse.setVerified(false);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            VerifyResponse errorResponse = new VerifyResponse();
            errorResponse.setVerified(false);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/receipts/{tokenId}/{serial}")
    public ResponseEntity<VerifyResponse> getReceiptMetadata(@PathVariable String tokenId, @PathVariable long serial) {
        try {
            VerifyResponse response = hederaService.getReceiptMetadata(tokenId, serial);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TimeoutException e) {
            e.printStackTrace();
            VerifyResponse errorResponse = new VerifyResponse();
            errorResponse.setVerified(false);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (PrecheckStatusException | ReceiptStatusException | IllegalArgumentException e) {
            e.printStackTrace();
            VerifyResponse errorResponse = new VerifyResponse();
            errorResponse.setVerified(false);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            VerifyResponse errorResponse = new VerifyResponse();
            errorResponse.setVerified(false);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/owner-receipts/{accountId}")
    public ResponseEntity<List<ReceiptRecord>> getOwnerReceipts(@PathVariable String accountId) {
        List<ReceiptRecord> records = hederaService.getOwnerReceipts(accountId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @GetMapping("/token-info/{tokenId}")
    public ResponseEntity<TokenInfoResponse> getTokenInfo(@PathVariable String tokenId) {
        try {
            TokenInfoResponse response = hederaService.getTokenInfo(tokenId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (PrecheckStatusException | ReceiptStatusException | IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/receipt-metadata-template")
    public ResponseEntity<NftReceiptMetadataTemplate> getReceiptMetadataTemplate() {
        return new ResponseEntity<>(new NftReceiptMetadataTemplate(), HttpStatus.OK);
    }
}