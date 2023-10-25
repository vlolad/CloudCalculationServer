package com.sergeev.cloudcalculation.model;

import com.sergeev.cloudcalculation.util.BlockType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class Block {

    private String value;
    private final BlockType type;
    private Block prev;
    private Block next;

    public Block(BlockType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Block{" +
                "value='" + value + '\'' +
                ", type=" + type +
                '}';
    }
}
