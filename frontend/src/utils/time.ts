export const formatDateHeader = (targetDate: Date) => {
  const month = targetDate.getMonth() + 1;
  const date = targetDate.getDate();
  const dayList = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'];
  const day = dayList[targetDate.getDay()];

  return `${month}월 ${date}일 ${day}`;
};

export const minutesToHours = (time: number) => {
  if (time === 0) {
    return `${time}분`;
  }

  const sign = time < 0 ? '-' : '';
  const absTime = Math.abs(time);

  const hour = Math.floor(absTime / 60);
  const minute = absTime % 60;

  return `${sign}${hour > 0 ? `${hour}시간` : ''} ${minute > 0 ? `${minute}분` : ''}`;
};
