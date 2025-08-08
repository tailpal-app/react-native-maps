// @ts-nocheck
import type {HostComponent} from 'react-native';
import type {ViewProps} from 'react-native';
import type {Int32} from 'react-native/Libraries/Types/CodegenTypes';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

export interface MarkerFabricNativeProps extends ViewProps {
  /**
   * @platform iOS: Apple Maps only
   * @platform Android: Supported
   */
  pathTemplate: string;

  /**
   * @platform iOS: Apple Maps only
   * @platform Android: Supported
   */
  tileSize?: Int32;

  /**
   * Set to true to use pathTemplate to open files from Android's AssetManager. The default is false.
   * @platform android
   */
  useAssets?: boolean;
}

export default codegenNativeComponent<MarkerFabricNativeProps>(
  'RNMapsLocalTile',
  {},
) as HostComponent<MarkerFabricNativeProps>;
