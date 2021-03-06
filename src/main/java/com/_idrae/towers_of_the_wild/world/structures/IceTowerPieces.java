package com._idrae.towers_of_the_wild.world.structures;

import com._idrae.towers_of_the_wild.TowersOfTheWild;
import com._idrae.towers_of_the_wild.config.TowersOfTheWildConfig;
import com._idrae.towers_of_the_wild.util.RegistryHandler;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.fml.ModList;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class IceTowerPieces {

    private static final ResourceLocation TOWER_TOP = ModList.get().isLoaded("waystones") && TowersOfTheWildConfig.waystonesCompat ? new ResourceLocation(TowersOfTheWild.MOD_ID, "waystone_tower_top") : new ResourceLocation(TowersOfTheWild.MOD_ID, "tower_top");
    private static final ResourceLocation ICE_TOWER_BOTTOM = new ResourceLocation(TowersOfTheWild.MOD_ID, "ice_tower_bottom");

    private static final ResourceLocation TOWER_CHEST = new ResourceLocation(TowersOfTheWild.MOD_ID, "chests/tower_chest");
    private static final Map<ResourceLocation, BlockPos> CENTER_TOP_OFFSETS = ImmutableMap.of(TOWER_TOP, new BlockPos(6, 28, 6), ICE_TOWER_BOTTOM, new BlockPos(5, 31, 5));
    private static final Map<ResourceLocation, BlockPos> CORNER_RELATIVE_POSITIONS = ImmutableMap.of(TOWER_TOP, new BlockPos(-1, 31, -1), ICE_TOWER_BOTTOM, BlockPos.ZERO);


    public static void addPieces(TemplateManager templateManager, BlockPos absolutePos, Rotation rotation, List<StructurePiece> pieces, Random random, NoFeatureConfig config) {
        pieces.add(new IceTowerPieces.Piece(templateManager, ICE_TOWER_BOTTOM, absolutePos, rotation));
        pieces.add(new IceTowerPieces.Piece(templateManager, TOWER_TOP, absolutePos, rotation));
    }

    public static class Piece extends TemplateStructurePiece {
        private final ResourceLocation structurePart;
        private final Rotation rotation;

        public Piece(TemplateManager templateManager, ResourceLocation structurePart, BlockPos absolutePos, Rotation rotation) {
            super(RegistryHandler.ICE_TOWER_PIECE, 0);
            this.structurePart = structurePart;
            BlockPos relativePos = CORNER_RELATIVE_POSITIONS.get(structurePart);
            this.templatePosition = absolutePos.add(relativePos.getX(), relativePos.getY(), relativePos.getZ());
            this.rotation = rotation;
            this.func_207614_a(templateManager);
        }

        public Piece(TemplateManager p_i50566_1_, CompoundNBT p_i50566_2_) {
            super(RegistryHandler.ICE_TOWER_PIECE, p_i50566_2_);
            this.structurePart = new ResourceLocation(p_i50566_2_.getString("Template"));
            this.rotation = Rotation.valueOf(p_i50566_2_.getString("Rot"));
            this.func_207614_a(p_i50566_1_);
        }

        private void func_207614_a(TemplateManager templateManager) {
            Template template = templateManager.getTemplateDefaulted(this.structurePart);
            // PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setCenterOffset(CENTER_TOP_OFFSETS.get(this.structurePart)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
            PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setCenterOffset(CENTER_TOP_OFFSETS.get(this.structurePart));
            this.setup(template, this.templatePosition, placementsettings);
        }

        /**
         * (abstract) Helper method to read subclass data from NBT
         */
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putString("Template", this.structurePart.toString());
            tagCompound.putString("Rot", this.rotation.name());
        }


        protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
            if ("chest".equals(function)) {
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                TileEntity tileentity = worldIn.getTileEntity(pos.down());
                if (tileentity instanceof ChestTileEntity) {
                    ((ChestTileEntity)tileentity).setLootTable(TOWER_CHEST, rand.nextLong());
                }

            }
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {
            // PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setCenterOffset(CENTER_TOP_OFFSETS.get(this.structurePart)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
            PlacementSettings placementsettings = (new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setCenterOffset(CENTER_TOP_OFFSETS.get(this.structurePart)));
            BlockPos relativePos = CORNER_RELATIVE_POSITIONS.get(this.structurePart);

            if (this.structurePart.equals(ICE_TOWER_BOTTOM)) {
                BlockPos blockpos1 = this.templatePosition;
                // BlockPos blockpos1 = this.templatePosition.add(Template.transformedBlockPos(placementsettings, new BlockPos(- relativePos.getX(), 0, - relativePos.getZ())));*

                // setting spawn height
                int height;
                int minHeight = Integer.MAX_VALUE;
                for (int i=1; i<8; ++i) {
                    for (int j=1; j<8; ++j) {
                        height = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockpos1.getX() + i, blockpos1.getZ() + j);
                        if (height < minHeight) {
                            minHeight = height;
                        }
                    }
                }

                // replacing dirt blocks beneath tower by grass
                for (int i=0; i<9; ++i) {
                    for (int j=0; j<9; ++j) {
                        BlockPos grassPos = new BlockPos(blockpos1.getX() + i, minHeight -1, blockpos1.getZ() + j);
                        BlockState blockstate = worldIn.getBlockState(grassPos);
                        if (blockstate.getBlock() == Blocks.DIRT) {
                            worldIn.setBlockState(grassPos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
                            // setting snow
                        }

                        if (!((i == 0 ||  i == 8) && (j == 0 || j == 8))) {
                            if (blockstate.getBlock() == Blocks.WATER) {
                                worldIn.setBlockState(grassPos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
                            }
                        }

                        BlockPos snowPos = grassPos.add(0, 1, 0);
                        if (worldIn.getBlockState(snowPos).getBlock() == Blocks.AIR) {
                            worldIn.setBlockState(snowPos, Blocks.SNOW.getDefaultState(), 3);
                        }


                    }
                }
                this.templatePosition = this.templatePosition.add(0, minHeight - 90, 0);

            } else if (this.structurePart.equals(TOWER_TOP)) {
                BlockPos blockpos1 = this.templatePosition;
                // BlockPos blockpos1 = this.templatePosition.add(Template.transformedBlockPos(placementsettings, new BlockPos(- relativePos.getX(), 0, - relativePos.getZ())));
                int height;
                int minHeight = Integer.MAX_VALUE;
                for (int i=1; i<8; ++i) {
                    for (int j=1; j<8; ++j) {
                        height = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockpos1.getX() + 1 + i, blockpos1.getZ() + 1 + j);
                        if (height < minHeight) {
                            minHeight = height;
                        }
                    }
                }
                this.templatePosition = this.templatePosition.add(0, minHeight - 90, 0);
            }
            return super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn, chunkPosIn);
        }
    }
}
