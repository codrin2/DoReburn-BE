import { describe, expect, it } from 'vitest';

import { getRouteInfoWithSwitched } from './MainPage.utils';

describe('출도착지 변경 여부와 출도착지 전환 여부에 따라 변경되는 getRouteInfoWithSwitched 테스트', () => {
  const memberAddress = {
    homeTitle: '반포 자이',
    homeXCoordinate: 127.027625,
    homeYCoordinate: 37.561268,
    schoolTitle: '가천대학교 글로벌캠퍼스교육대학원',
    schoolXCoordinate: 127.032412,
    schoolYCoordinate: 37.561268,
  };

  describe('출발지와 도착지 둘다 변경되었을 경우', () => {
    it('변경된 주소가 그대로 반환된다.', () => {
      const startAddress = { startX: 1, startY: 2, startName: '출발지 테스트' };
      const endAddress = { endX: 3, endY: 4, endName: '목적지 테스트' };
      const result = getRouteInfoWithSwitched(false, startAddress, endAddress, memberAddress);

      expect(result).toEqual({
        startX: startAddress.startX,
        startY: startAddress.startY,
        startName: startAddress.startName,
        endX: endAddress.endX,
        endY: endAddress.endY,
        endName: endAddress.endName,
      });
    });

    it('출도착지 전환 버튼 클릭 시 임의로 정한 출도착지를 반대로 전환된다.', () => {
      const startAddress = { startX: 1, startY: 2, startName: '출발지 테스트' };
      const endAddress = { endX: 3, endY: 4, endName: '목적지 테스트' };
      const result = getRouteInfoWithSwitched(true, startAddress, endAddress, memberAddress);

      expect(result).toEqual({
        startX: endAddress.endX,
        startY: endAddress.endY,
        startName: endAddress.endName,
        endX: startAddress.startX,
        endY: startAddress.startY,
        endName: startAddress.startName,
      });
    });
  });

  describe('출발지만 변경되었을 경우', () => {
    it('출발지는 변경된 출발 주소, 도착지는 학교 주소로 바뀐다.', () => {
      const startAddress = { startX: 1, startY: 2, startName: '출발지 테스트' };
      const endAddress = { endX: 0, endY: 0, endName: '' };
      const result = getRouteInfoWithSwitched(false, startAddress, endAddress, memberAddress);

      expect(result).toEqual({
        startX: startAddress.startX,
        startY: startAddress.startY,
        startName: startAddress.startName,
        endX: memberAddress.schoolXCoordinate,
        endY: memberAddress.schoolYCoordinate,
        endName: memberAddress.schoolTitle,
      });
    });

    it('출도착지 전환 버튼 클릭 시 출발지는 학교, 도착지는 변경된 출발 주소로 전환된다.', () => {
      const startAddress = { startX: 1, startY: 2, startName: '출발지 테스트' };
      const endAddress = { endX: 0, endY: 0, endName: '' };
      const result = getRouteInfoWithSwitched(true, startAddress, endAddress, memberAddress);

      expect(result).toEqual({
        startX: memberAddress.schoolXCoordinate,
        startY: memberAddress.schoolYCoordinate,
        startName: memberAddress.schoolTitle,
        endX: startAddress.startX,
        endY: startAddress.startY,
        endName: startAddress.startName,
      });
    });
  });

  describe('도착지만 변경되었을 경우', () => {
    it('출발지는 집 주소, 도착지는 변경된 도착지 주소로 바뀐다.', () => {
      const startAddress = { startX: 0, startY: 0, startName: '' };
      const endAddress = { endX: 3, endY: 4, endName: '목적지 테스트' };
      const result = getRouteInfoWithSwitched(false, startAddress, endAddress, memberAddress);

      expect(result).toEqual({
        startX: memberAddress.homeXCoordinate,
        startY: memberAddress.homeYCoordinate,
        startName: memberAddress.homeTitle,
        endX: endAddress.endX,
        endY: endAddress.endY,
        endName: endAddress.endName,
      });
    });

    it('출도착지 전환 버튼 클릭 시 출발지는 변경된 도착지 주소, 도착지는 집으로 전환된다.', () => {
      const startAddress = { startX: 0, startY: 0, startName: '' };
      const endAddress = { endX: 3, endY: 4, endName: '목적지 테스트' };
      const result = getRouteInfoWithSwitched(true, startAddress, endAddress, memberAddress);

      expect(result).toEqual({
        startX: endAddress.endX,
        startY: endAddress.endY,
        startName: endAddress.endName,
        endX: memberAddress.homeXCoordinate,
        endY: memberAddress.homeYCoordinate,
        endName: memberAddress.homeTitle,
      });
    });
  });

  describe('출발지와 도착지가 모두 변경되지 않았을 경우', () => {
    it('출발지는 집 주소, 도착지는 학교 주소로 그대로 반환된다.', () => {
      const startAddress = { startX: 0, startY: 0, startName: '' };
      const endAddress = { endX: 0, endY: 0, endName: '' };
      const result = getRouteInfoWithSwitched(false, startAddress, endAddress, memberAddress);

      expect(result).toEqual({
        startX: memberAddress.homeXCoordinate,
        startY: memberAddress.homeYCoordinate,
        startName: memberAddress.homeTitle,
        endX: memberAddress.schoolXCoordinate,
        endY: memberAddress.schoolYCoordinate,
        endName: memberAddress.schoolTitle,
      });
    });

    it('출도착지 전환 버튼 클릭 시 출발지는 학교 주소, 도착지는 집 주소로 전환된다.', () => {
      const startAddress = { startX: 0, startY: 0, startName: '' };
      const endAddress = { endX: 0, endY: 0, endName: '' };
      const result = getRouteInfoWithSwitched(true, startAddress, endAddress, memberAddress);

      expect(result).toEqual({
        startX: memberAddress.schoolXCoordinate,
        startY: memberAddress.schoolYCoordinate,
        startName: memberAddress.schoolTitle,
        endX: memberAddress.homeXCoordinate,
        endY: memberAddress.homeYCoordinate,
        endName: memberAddress.homeTitle,
      });
    });
  });
});
