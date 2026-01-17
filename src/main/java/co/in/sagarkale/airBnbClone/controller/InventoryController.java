package co.in.sagarkale.airBnbClone.controller;

import co.in.sagarkale.airBnbClone.dto.InventoryDto;
import co.in.sagarkale.airBnbClone.dto.UpdateInventoryReqDto;
import co.in.sagarkale.airBnbClone.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/inventory")
@Tag(name = "Inventory", description = "Admin level endpoints for inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "Get all inventories for room by room id")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoomId(@PathVariable Long roomId){
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoomId(roomId));
    }

    @PatchMapping("/rooms/{roomId}")
    @Operation(summary = "Update the inventory of room")
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId, @RequestBody UpdateInventoryReqDto updateInventoryReqDto){
        inventoryService.updateInventory(roomId, updateInventoryReqDto);
        return ResponseEntity.noContent().build();
    }

}
