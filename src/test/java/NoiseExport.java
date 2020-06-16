import net.rgsw.ptg.noise.Noise2D;
import net.rgsw.ptg.noise.opensimplex.FractalOpenSimplex2D;
import net.rgsw.ptg.noise.opensimplex.InverseFractalOpenSimplex2D;
import net.rgsw.ptg.noise.opensimplex.OpenSimplex2D;
import net.rgsw.ptg.noise.perlin.FractalPerlin2D;
import net.rgsw.ptg.noise.perlin.InverseFractalPerlin2D;
import net.rgsw.ptg.noise.perlin.Perlin2D;
import net.rgsw.ptg.noise.perlin.RepetitivePerlin2D;
import net.rgsw.ptg.noise.util.NoiseMath;
import net.rgsw.ptg.region.CachingRegionContext;
import net.rgsw.ptg.region.Region;
import net.rgsw.ptg.region.RegionBuilder;
import net.rgsw.ptg.region.RegionContext;
import net.rgsw.ptg.region.layer.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class NoiseExport {
    private NoiseExport() {
    }

    public static void main( String[] args ) {
        int seed = 616249123;
        exportNoise( new File( "exports/perlin.png" ), 256, 256, new Perlin2D( seed ), 16 );
        exportNoise( new File( "exports/opensimplex.png" ), 256, 256, new OpenSimplex2D( seed ), 16 );
        exportNoise( new File( "exports/fractalperlin.png" ), 256, 256, new FractalPerlin2D( seed, 8 ), 16 );
        exportNoise( new File( "exports/fractalopensimplex.png" ), 256, 256, new FractalOpenSimplex2D( seed, 8 ), 16 );
        exportNoise( new File( "exports/inversefractalperlin.png" ), 256, 256, new InverseFractalPerlin2D( seed, 8 ), 16 );
        exportNoise( new File( "exports/inversefractalopensimplex.png" ), 256, 256, new InverseFractalOpenSimplex2D( seed, 8 ), 16 );
        exportNoise( new File( "exports/repetitiveperlin.png" ), 256, 256, new RepetitivePerlin2D( seed, 8 ), 16 );
        exportNoise( new File( "exports/random.png" ), 256, 256, Noise2D.random( seed ), 16 );

        RegionContext<?> ctx = new CachingRegionContext( 25, 55122121599L );
        Region reg = ctx.randomF( - 1, 1, 3417L )
                        .zoomFuzzy( 2 )
                        .zoom( 3 )
                        .smooth()
                        .buildRegion();
        exportNoise( new File( "exports/region.png" ), 256, 256, Noise2D.region( reg ), 1 );

        int[] blackWhite = { 0x000000, 0xFFFFFF };
        int[] redGreenBlueYellow = { 0xFF0000, 0xFFFF00, 0x00FF00, 0x0000FF };

        exportLayerExample( "zoom", ctx.pick( redGreenBlueYellow ), ZoomLayer.INSTANCE, 16, 8 );
        exportLayerExample( "fuzzy_zoom", ctx.pick( redGreenBlueYellow ), FuzzyZoomLayer.INSTANCE, 16, 8 );
        exportLayerExample( "voronoi_zoom", ctx.pick( redGreenBlueYellow ), VoronoiZoomLayer.INSTANCE, 16, 8 );
        exportLayerExample( "cell_zoom", ctx.pick( redGreenBlueYellow ), CellZoomLayer.INSTANCE, 16, 8 );
        exportFloatLayerExample( "interpolate_zoom", ctx.pickF( 0, 1 ), InterpolateZoomLayer.FLOAT, 16, 8 );
        exportLayerExample( "smooth", ctx.pick( 0xFF0000, 0xFF0000, 0xFFFF00, 0x0000FF ), SmoothingLayer.INSTANCE, 16, 8 );
        exportLayerExample( "replace", ctx.pick( redGreenBlueYellow ), new ReplaceLayer( 0x00FF00, 0x00FFFF ), 16, 8 );
        exportLayerExample( "replace_merge", ctx.pick( redGreenBlueYellow ), ctx.pick( blackWhite, 13125 ), new ReplaceMergeLayer( 0x00FF00 ), 16, 8 );

        exportLayerExample( "zoom_small", ctx.pick( redGreenBlueYellow ).zoom( 3 ), ZoomLayer.INSTANCE, 128, 1 );
        exportLayerExample( "fuzzy_zoom_small", ctx.pick( redGreenBlueYellow ).zoom( 3 ), FuzzyZoomLayer.INSTANCE, 128, 1 );
        exportLayerExample( "voronoi_zoom_small", ctx.pick( redGreenBlueYellow ).zoom( 3 ), VoronoiZoomLayer.INSTANCE, 128, 1 );
        exportLayerExample( "cell_zoom_small", ctx.pick( redGreenBlueYellow ).zoom( 3 ), CellZoomLayer.INSTANCE, 128, 1 );
        exportFloatLayerExample( "interpolate_zoom_small", ctx.pickF( 0, 1 ).zoom( 3 ), InterpolateZoomLayer.FLOAT, 128, 1 );

        exportLayerExample( "random", ctx.pick( redGreenBlueYellow ), 16, 8 );
        exportFloatLayerExample( "float_random", ctx.randomF( 0, 1 ), 16, 8 );
        exportLayerExample( "static", ctx.value( 0xFF0000 ), 16, 8 );
        exportFloatLayerExample( "noise", ctx.noise( new FractalOpenSimplex2D( seed, 8, 8, 4 ).lerp( 0, 1 ) ), 16, 8 );
        exportFloatLayerExample( "noise_small", ctx.noise( new FractalOpenSimplex2D( seed, 8, 8, 4 ).lerp( 0, 1 ) ).zoom( 2 ), 128, 1 );
    }

    public static void exportLayerExample( String type, RegionBuilder<?, ?> factory, TransformerLayer layer, int size, int px ) {
        Region before = factory.buildRegion();
        Region after = factory.transform( layer ).buildRegion();

        exportRegion( new File( "exports/regions/" + type + "_before.png" ), size * px, size * px, before, px );
        exportRegion( new File( "exports/regions/" + type + "_after.png" ), size * px, size * px, after, px );
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public static void exportLayerExample( String type, RegionBuilder<?, ?> factory1, RegionBuilder<?, ?> factory2, MergerLayer layer, int size, int px ) {
        Region before1 = factory1.buildRegion();
        Region before2 = factory2.buildRegion();
        Region after = ( (RegionBuilder) factory1 ).merge( layer, factory2 ).buildRegion();

        exportRegion( new File( "exports/regions/" + type + "_before1.png" ), size * px, size * px, before1, px );
        exportRegion( new File( "exports/regions/" + type + "_before2.png" ), size * px, size * px, before2, px );
        exportRegion( new File( "exports/regions/" + type + "_after.png" ), size * px, size * px, after, px );
    }

    public static void exportLayerExample( String type, RegionBuilder<?, ?> factory, int size, int px ) {
        Region reg = factory.buildRegion();

        exportRegion( new File( "exports/regions/" + type + ".png" ), size * px, size * px, reg, px );
    }

    public static void exportFloatLayerExample( String type, RegionBuilder<?, ?> factory, TransformerLayer layer, int size, int px ) {
        Region before = factory.buildRegion();
        Region after = factory.transform( layer ).buildRegion();

        exportFloatRegion( new File( "exports/regions/" + type + "_before.png" ), size * px, size * px, before, px );
        exportFloatRegion( new File( "exports/regions/" + type + "_after.png" ), size * px, size * px, after, px );
    }

    public static void exportFloatLayerExample( String type, RegionBuilder<?, ?> factory, int size, int px ) {
        Region reg = factory.buildRegion();

        exportFloatRegion( new File( "exports/regions/" + type + ".png" ), size * px, size * px, reg, px );
    }

    public static void exportNoise( File file, int width, int height, Noise2D noise, double scale ) {
        System.out.println( "Exporting: " + file );

        file.getParentFile().mkdirs();

        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        for( int x = 0; x < width; x++ ) {
            for( int y = 0; y < height; y++ ) {
                double n = NoiseMath.unlerp( - 1, 1, noise.generate( x / scale, y / scale ) );
                int v = (int) NoiseMath.clamp( 0, 255, n * 255 );
                int col = 0xFF000000 | v << 16 | v << 8 | v;
                image.setRGB( x, y, col );
            }
        }

        try {
            ImageIO.write( image, "PNG", file );
        } catch( IOException exc ) {
            throw new RuntimeException( exc );
        }
    }

    public static void exportFloatRegion( File file, int width, int height, Region region, int scale ) {
        System.out.println( "Exporting: " + file );

        file.getParentFile().mkdirs();

        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        for( int x = 0; x < width; x++ ) {
            for( int y = 0; y < height; y++ ) {
                double n = region.getFPValue( x / scale, y / scale );
                int v = (int) NoiseMath.clamp( 0, 255, n * 255 );
                int col = 0xFF000000 | v << 16 | v << 8 | v;
                image.setRGB( x, y, col );
            }
        }

        try {
            ImageIO.write( image, "PNG", file );
        } catch( IOException exc ) {
            throw new RuntimeException( exc );
        }
    }


    public static void exportRegion( File file, int width, int height, Region noise, int scale ) {
        System.out.println( "Exporting: " + file );

        file.getParentFile().mkdirs();

        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        for( int x = 0; x < width; x++ ) {
            for( int y = 0; y < height; y++ ) {
                int v = noise.getValue( x / scale, y / scale );
                int col = 0xFF000000 | v;
                image.setRGB( x, y, col );
            }
        }

        try {
            ImageIO.write( image, "PNG", file );
        } catch( IOException exc ) {
            throw new RuntimeException( exc );
        }
    }
}