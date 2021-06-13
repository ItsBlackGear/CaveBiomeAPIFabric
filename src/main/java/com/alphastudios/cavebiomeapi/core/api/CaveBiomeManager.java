package com.alphastudios.cavebiomeapi.core.api;

import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

//<>

public class CaveBiomeManager {
    private static ArrayList<BiomeEntry>[] biomes;



    public static class BiomeEntry extends WeightedPicker.Entry {
        private final RegistryKey<Biome> biome;

        public BiomeEntry(RegistryKey<Biome> biome, int weight) {
            super(weight);
            this.biome = biome;
        }

        public RegistryKey<Biome> getBiome() {
            return this.biome;
        }
    }

    private static class TrackedList<E> extends ArrayList<E> {
        private static final long serialVersionUID = 1L;
        private boolean isModded = false;

        @SafeVarargs
        private <T extends E> TrackedList(T... c) {
            super(Arrays.asList(c));
        }

        @Override
        public E set(int index, E element) {
            this.isModded = true;
            return super.set(index, element);
        }

        @Override
        public boolean add(E e) {
            this.isModded = true;
            return super.add(e);
        }

        @Override
        public void add(int index, E element) {
            this.isModded = true;
            super.add(index, element);
        }

        @Override
        public E remove(int index) {
            this.isModded = true;
            return super.remove(index);
        }

        @Override
        public boolean remove(Object o) {
            this.isModded = true;
            return super.remove(o);
        }

        @Override
        public void clear() {
            this.isModded = true;
            super.clear();
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            this.isModded = true;
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            this.isModded = true;
            return super.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            this.isModded = true;
            return super.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            this.isModded = true;
            return super.retainAll(c);
        }

        public boolean isModded() {
            return this.isModded;
        }
    }
}