package com._idrae.towers_of_the_wild.world.structures;

import com._idrae.towers_of_the_wild.TowersOfTheWild;
import com._idrae.towers_of_the_wild.config.TowersOfTheWildConfig;
import com._idrae.towers_of_the_wild.util.RegistryHandler;
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.fml.ModList;

import java.util.Random;
import java.util.function.Function;

public class TowerStructure extends ScatteredStructure<NoFeatureConfig> {

    public static final String NAME = TowersOfTheWild.MOD_ID +  ":Tower";
    private static int FEATURE_DISTANCE;
    private static final int FEATURE_SEPARATION = 5;

    public TowerStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51491_1_) {
        super(p_i51491_1_);
    }

    public String getStructureName() {
        return NAME;
    }

    public int getSize() {
        return 3;
    }

    @Override
    protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> generator, Random random, int chunkX, int chunkZ, int offsetX, int offsetZ) {
        FEATURE_DISTANCE = TowersOfTheWildConfig.rarity;
        int chunkPosX = chunkX + FEATURE_DISTANCE * offsetX;
        int chunkPosZ = chunkZ + FEATURE_DISTANCE * offsetZ;
        int chunkPosX1 = chunkPosX < 0 ? chunkPosX - FEATURE_DISTANCE + 1 : chunkPosX;
        int chunkPosZ1 = chunkPosZ < 0 ? chunkPosZ - FEATURE_DISTANCE + 1 : chunkPosZ;
        int lvt_13_1_ = chunkPosX1 / FEATURE_DISTANCE;
        int lvt_14_1_ = chunkPosZ1 / FEATURE_DISTANCE;
        ((SharedSeedRandom)random).setLargeFeatureSeedWithSalt(generator.getSeed(), lvt_13_1_, lvt_14_1_, 16897777);
        lvt_13_1_ *= FEATURE_DISTANCE;
        lvt_14_1_ *= FEATURE_DISTANCE;
        lvt_13_1_ += random.nextInt(FEATURE_DISTANCE - FEATURE_SEPARATION);
        lvt_14_1_ += random.nextInt(FEATURE_DISTANCE - FEATURE_SEPARATION);
        return new ChunkPos(lvt_13_1_, lvt_14_1_);
    }

    /*
    @Override
    public boolean canBeGenerated(BiomeManager manager, ChunkGenerator<?> generator, Random random, int chunkX, int chunkZ, Biome biome) {
        ChunkPos chunkpos = this.getStartPositionForPosition(generator, random, chunkX, chunkZ, 0, 0);
        if (chunkX == chunkpos.x && chunkZ == chunkpos.z) {
            for(Biome biome1 : generator.getBiomeProvider().getBiomes(chunkX * 16 + 9, generator.getSeaLevel(), chunkZ * 16 + 9, 32)) {
                if (!generator.hasStructure(biome1, this)) {
                    return false;
                }
            }
            Random random1 = new Random((long)(chunkX + chunkZ * 10387313));
            Rotation rotation = Rotation.values()[random1.nextInt(Rotation.values().length)];
            int i = 5;
            int j = 5;
            if (rotation == Rotation.CLOCKWISE_90) {
                i = -5;
            } else if (rotation == Rotation.CLOCKWISE_180) {
                i = -5;
                j = -5;
            } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
                j = -5;
            }

            int k = (chunkX << 4) + 7;
            int l = (chunkZ << 4) + 7;
            int i1 = generator.func_222531_c(k, l, Heightmap.Type.WORLD_SURFACE_WG);
            int j1 = generator.func_222531_c(k, l + j, Heightmap.Type.WORLD_SURFACE_WG);
            int k1 = generator.func_222531_c(k + i, l, Heightmap.Type.WORLD_SURFACE_WG);
            int l1 = generator.func_222531_c(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG);
            int minHeight = Math.min(Math.min(i1, j1), Math.min(k1, l1));
            int maxHeight = Math.max(Math.max(i1, j1), Math.max(k1, l1));
            if (maxHeight - minHeight < 2 && maxHeight - minHeight > -2) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

     */

    public Structure.IStartFactory getStartFactory() {
        return Start::new;
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        return super.place(worldIn, generator, rand, pos, config);
    }

    protected int getSeedModifier() {
        return 16897777;
    }

    public static class Start extends StructureStart {
        public Start(Structure<?> structure, int chunkX, int chunkY, Biome biome, MutableBoundingBox boundingBox, int reference, long seed) {
            super(structure, chunkX, chunkY, biome, boundingBox, reference, seed);
        }

        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            NoFeatureConfig nofeatureconfig = (NoFeatureConfig)generator.getStructureConfig(biomeIn, RegistryHandler.TOWER.get());
            int i = chunkX * 16;
            int j = chunkZ * 16;
            BlockPos blockpos = new BlockPos(i + 3, 90, j + 3);
            // Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
            Rotation rotation = Rotation.NONE;

            if (biomeIn.getCategory() == Biome.Category.JUNGLE) {
                JungleTowerPieces.addPieces(templateManagerIn, blockpos, rotation, this.components, this.rand, nofeatureconfig);
            } else if (biomeIn.getCategory() == Biome.Category.ICY) {
                // blockpos.add(-2, 0, -2);
                IceTowerPieces.addPieces(templateManagerIn, blockpos, rotation, this.components, this.rand, nofeatureconfig);
            } else {
                if (this.rand.nextInt(100) < TowersOfTheWildConfig.derelictTowerProportion) {
                    // blockpos.add(-2, 0, -2);
                    blockpos = new BlockPos(i, 90, j);
                    if (biomeIn.getCategory() == Biome.Category.PLAINS
                            || biomeIn.getCategory() == Biome.Category.FOREST
                            || biomeIn.getCategory() == Biome.Category.TAIGA
                            || biomeIn.getCategory() == Biome.Category.SAVANNA
                            || biomeIn.getCategory() == Biome.Category.EXTREME_HILLS) {
                        DerelictTowerGrassPieces.addPieces(templateManagerIn, blockpos, rotation, this.components, this.rand, nofeatureconfig);
                    } else {
                        DerelictTowerPieces.addPieces(templateManagerIn, blockpos, rotation, this.components, this.rand, nofeatureconfig);
                    }
                } else {
                    TowerPieces.addPieces(templateManagerIn, blockpos, rotation, this.components, this.rand, nofeatureconfig);
                }
            }
            this.recalculateStructureSize();
        }
    }
}
