import type {TurboModule} from 'react-native';
import {TurboModuleRegistry} from 'react-native';
import type {Double, Int32} from 'react-native/Libraries/Types/CodegenTypes';
import type {Camera} from './NativeComponentMapView';
import type {Address} from '../MapView.types';

type LatLng = {
  latitude: Double;
  longitude: Double;
};

type Point = Readonly<{
  x: Double; // Non-nullable Double for x
  y: Double; // Non-nullable Double for y
}>;

export type Region = Readonly<
  LatLng & {
    latitudeDelta: Double; // Non-nullable Double for latitudeDelta
    longitudeDelta: Double; // Non-nullable Double for longitudeDelta
  }
>;

export type MapBoundaries = {northEast: LatLng; southWest: LatLng};

export interface Spec extends TurboModule {
  getCamera(tag: Int32): Promise<Camera>;
  getMarkersFrames(tag: Int32, onlyVisible: boolean): Promise<unknown>;
  getMapBoundaries(tag: Int32): Promise<MapBoundaries>;
  takeSnapshot(tag: Int32, config: string): Promise<string>;
  getAddressFromCoordinates(tag: Int32, coordinate: LatLng): Promise<Address>;
  getPointForCoordinate(tag: Int32, coordinate: LatLng): Promise<Point>;
  getCoordinateForPoint(tag: Int32, point: Point): Promise<LatLng>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNMapsAirModule');
