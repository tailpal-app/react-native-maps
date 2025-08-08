// @ts-nocheck
import type {HostComponent} from 'react-native';
import type {ViewProps} from 'react-native';
import type {Double, Int32} from 'react-native/Libraries/Types/CodegenTypes';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

type WeightedLatLng = {
  latitude: Double;
  longitude: Double;
  weight?: Double;
};

type Gradient = {
  /**
   * Resolution of color map -- number corresponding to the number of steps colors are interpolated into.
   *
   * @default 256
   * @platform iOS: Google Maps only
   * @platform Android: Supported
   */
  colorMapSize: Int32;

  /**
   * Colors (one or more) to used for gradient.
   *
   * @platform iOS: Google Maps only
   * @platform Android: Supported
   */
  colors: string[];

  /**
   * Array of floating point values from 0 to 1 representing where each color starts.
   *
   * Array length must be equal to `colors` array length.
   *
   * @platform iOS: Google Maps only
   * @platform Android: Supported
   */
  startPoints: ReadonlyArray<Double>;
};

export interface MarkerFabricNativeProps extends ViewProps {
  gradient?: Gradient;

  /**
   * The opacity of the heatmap.
   *
   * @default 0.7
   * @platform iOS: Google Maps only
   * @platform Android: Supported
   */
  opacity?: Double;

  /**
   * Array of heatmap entries to apply towards density.
   *
   * @platform iOS: Google Maps only
   * @platform Android: Supported
   */
  points?: ReadonlyArray<WeightedLatLng>;

  /**
   * The radius of the heatmap points in pixels, between 10 and 50.
   *
   * @default 20
   * @platform iOS: Google Maps only
   * @platform Android: Supported
   */
  radius?: Double;
}

export default codegenNativeComponent<MarkerFabricNativeProps>(
  'RNMapsHeatmap',
  {},
) as HostComponent<MarkerFabricNativeProps>;
