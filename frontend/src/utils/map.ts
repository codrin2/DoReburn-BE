export const createMarkerImage = (src: string, width: number, height: number) => {
  const markerSrc = src;
  const markerSize = new window.kakao.maps.Size(width, height);
  const markerOption = { offset: new window.kakao.maps.Point(0, 0) };

  return new window.kakao.maps.MarkerImage(markerSrc, markerSize, markerOption);
};
