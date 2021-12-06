package com.alphastudios.cavebiomeapi.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererWeatherMixin {
	@Shadow @Final private static Identifier RAIN = new Identifier("textures/environment/rain.png");
	@Shadow @Final private static Identifier SNOW = new Identifier("textures/environment/snow.png");
	@Shadow @Final private MinecraftClient client;
	@Shadow private int ticks;
	@Shadow @Final private float[] field_20794;
	@Shadow @Final private float[] field_20795;
	
	@Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void renderWeatherCorrectly(LightmapTextureManager manager, float f, double d, double e, double g, CallbackInfo ci) {
      
	  if (this.client.world.isRaining()) {
		  BlockPos pos = this.client.getCameraEntity().getCameraBlockPos(); //get block camera is at
          Biome biome = this.client.world.getBiome(pos); //determine biome at pos
          BlockPos topPos = new BlockPos(pos.getX(), this.client.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY(), pos.getZ());
    	  float temp = biome.getTemperature(topPos);
    	  boolean weatherTransition = false;
    	  boolean weatherlessTransition = false;
    	  if (temp > 0.10F && temp < 0.20F) weatherTransition = true;
    	  if (temp > 1.00F && temp < 1.10F) weatherlessTransition = true;
    	  
          if (topPos.getY() < pos.getY()) {
              if (this.client.world.getDimension() != this.client.world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).get(DimensionType.THE_END_ID)) { //crashes in 1.16
                  if (temp >= 0.10F && temp <= 1.10F) {
                	  float h = this.client.world.getRainGradient(f);
                      if (!(h <= 0.0F)) {
                         manager.enable();
                         World world = this.client.world;
                         int i = MathHelper.floor(d);
                         int j = MathHelper.floor(e);
                         int k = MathHelper.floor(g);
                         Tessellator tessellator = Tessellator.getInstance();
                         BufferBuilder bufferBuilder = tessellator.getBuffer();
                         RenderSystem.disableCull();
                         RenderSystem.enableBlend();
                         RenderSystem.defaultBlendFunc();
                         RenderSystem.enableDepthTest();
                         int l = 5;
                         if (MinecraftClient.isFancyGraphicsOrBetter()) {
                            l = 10;
                         }

                         RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
                         int m = -1;
                         RenderSystem.setShader(GameRenderer::getParticleShader);
                         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                         for(int o = k - l; o <= k + l; ++o) {
                            for(int p = i - l; p <= i + l; ++p) {
                               int q = (o - k + 16) * 32 + p - i + 16;
                               double r = (double)this.field_20794[q] * 0.5D;
                               double s = (double)this.field_20795[q] * 0.5D;
                               if (biome.getPrecipitation() != Biome.Precipitation.NONE) {
                                  int t = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY();
                                  int u = j - l;
                                  int v = j + l;
                                  if (u < t) {
                                     u = t;
                                  }

                                  if (v < t) {
                                     v = t;
                                  }

                                  if (u != v) {
                                     Random random = new Random((long)(p * p * 3121 + p * 45238971 ^ o * o * 418711 + o * 13761));
                                     float z;
                                     float ad;

                                     if (m != 0) {
                                        if (m >= 0) {
                                           tessellator.draw();
                                        }

                                        m = 0;
                                        RenderSystem.setShaderTexture(0, RAIN);
                                        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                                     }

                                     int y = this.ticks + p * p * 3121 + p * 45238971 + o * o * 418711 + o * 13761 & 31;
                                     z = -((float)y + f) / 32.0F * (3.0F + random.nextFloat());
                                     double aa = (double)p + 0.5D - d;
                                     double ab = (double)o + 0.5D - g;
                                     float ac = (float)Math.sqrt(aa * aa + ab * ab) / (float)l;
                                     ad = ((1.0F - ac * ac) * 0.5F + 0.5F) * h;
                                     if (weatherTransition) ad = ad * (Math.min(0.10F, temp-0.10F)/0.10F);
                                     else if (weatherlessTransition) ad = ad * (1-(Math.min(0.10F, temp-0.10F)/0.10F));
                                     int ae = WorldRenderer.getLightmapCoordinates(world, pos);
                                     bufferBuilder.vertex((double)p - d - r + 0.5D, (double)v - e, (double)o - g - s + 0.5D).texture(0.0F, (float)u * 0.25F + z).color(1.0F, 1.0F, 1.0F, ad).light(ae).next();
                                     bufferBuilder.vertex((double)p - d + r + 0.5D, (double)v - e, (double)o - g + s + 0.5D).texture(1.0F, (float)u * 0.25F + z).color(1.0F, 1.0F, 1.0F, ad).light(ae).next();
                                     bufferBuilder.vertex((double)p - d + r + 0.5D, (double)u - e, (double)o - g + s + 0.5D).texture(1.0F, (float)v * 0.25F + z).color(1.0F, 1.0F, 1.0F, ad).light(ae).next();
                                     bufferBuilder.vertex((double)p - d - r + 0.5D, (double)u - e, (double)o - g - s + 0.5D).texture(0.0F, (float)v * 0.25F + z).color(1.0F, 1.0F, 1.0F, ad).light(ae).next();
                                  }
                               }
                            }
                         }

                         if (m >= 0) {
                            tessellator.draw();
                         }

                         RenderSystem.enableCull();
                         RenderSystem.disableBlend();
                         manager.disable();
                      }
                    } 
                  	
                    if (temp <= 0.20F) {
                	  float h = this.client.world.getRainGradient(f);
                      if (!(h <= 0.0F)) {
                         manager.enable();
                         World world = this.client.world;
                         int i = MathHelper.floor(d);
                         int j = MathHelper.floor(e);
                         int k = MathHelper.floor(g);
                         Tessellator tessellator = Tessellator.getInstance();
                         BufferBuilder bufferBuilder = tessellator.getBuffer();
                         RenderSystem.disableCull();
                         RenderSystem.enableBlend();
                         RenderSystem.defaultBlendFunc();
                         RenderSystem.enableDepthTest();
                         int l = 5;
                         if (MinecraftClient.isFancyGraphicsOrBetter()) {
                            l = 10;
                         }

                         RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
                         int m = -1;
                         float n = (float)this.ticks + f;
                         RenderSystem.setShader(GameRenderer::getParticleShader);
                         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                         for(int o = k - l; o <= k + l; ++o) {
                            for(int p = i - l; p <= i + l; ++p) {
                               int q = (o - k + 16) * 32 + p - i + 16;
                               double r = (double)this.field_20794[q] * 0.5D;
                               double s = (double)this.field_20795[q] * 0.5D;
                               if (biome.getPrecipitation() != Biome.Precipitation.NONE) {
                                  int t = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY();
                                  int u = j - l;
                                  int v = j + l;
                                  if (u < t) {
                                     u = t;
                                  }

                                  if (v < t) {
                                     v = t;
                                  }

                                  if (u != v) {
                                     Random random = new Random((long)(p * p * 3121 + p * 45238971 ^ o * o * 418711 + o * 13761));
                                     float z;
                                     float ad;
                                     
                                     if (m != 1) {
                                        if (m >= 0) {
                                           tessellator.draw();
                                        }

                                        m = 1;
                                        RenderSystem.setShaderTexture(0, SNOW);
                                        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                                     }

                                     float af = -((float)(this.ticks & 511) + f) / 512.0F;
                                     z = (float)(random.nextDouble() + (double)n * 0.01D * (double)((float)random.nextGaussian()));
                                     float ah = (float)(random.nextDouble() + (double)(n * (float)random.nextGaussian()) * 0.001D);
                                     double ai = (double)p + 0.5D - d;
                                     double aj = (double)o + 0.5D - g;
                                     ad = (float)Math.sqrt(ai * ai + aj * aj) / (float)l;
                                     float al = ((1.0F - ad * ad) * 0.3F + 0.5F) * h;
                                     if (weatherTransition) al = al * (1-(Math.min(0.10F, temp-0.10F)/0.10F));
                                     int am = WorldRenderer.getLightmapCoordinates(world, pos);
                                     int an = am >> 16 & '\uffff';
                                     int ao = am & '\uffff';
                                     int ap = (an * 3 + 240) / 4;
                                     int aq = (ao * 3 + 240) / 4;
                                     bufferBuilder.vertex((double)p - d - r + 0.5D, (double)v - e, (double)o - g - s + 0.5D).texture(0.0F + z, (float)u * 0.25F + af + ah).color(1.0F, 1.0F, 1.0F, al).light(aq, ap).next();
                                     bufferBuilder.vertex((double)p - d + r + 0.5D, (double)v - e, (double)o - g + s + 0.5D).texture(1.0F + z, (float)u * 0.25F + af + ah).color(1.0F, 1.0F, 1.0F, al).light(aq, ap).next();
                                     bufferBuilder.vertex((double)p - d + r + 0.5D, (double)u - e, (double)o - g + s + 0.5D).texture(1.0F + z, (float)v * 0.25F + af + ah).color(1.0F, 1.0F, 1.0F, al).light(aq, ap).next();
                                     bufferBuilder.vertex((double)p - d - r + 0.5D, (double)u - e, (double)o - g - s + 0.5D).texture(0.0F + z, (float)v * 0.25F + af + ah).color(1.0F, 1.0F, 1.0F, al).light(aq, ap).next();
                                  }
                               }
                            }
                         }

                         if (m >= 0) {
                            tessellator.draw();
                         }

                         RenderSystem.enableCull();
                         RenderSystem.disableBlend();
                         manager.disable();
                      }
                    }
                  }
              }
          }
	  	ci.cancel();
	  }
}