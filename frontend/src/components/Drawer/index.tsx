import { Link } from 'react-router';

import { DRAWER_MENU } from './Drawer.constants';
import * as S from './Drawer.styled';
import { useDrawer } from './hooks/useDrawer';

import Icon from '@/components/Icon';
import theme from '@/styles/theme';

const Drawer = () => {
  const { isOpen, closeDrawer } = useDrawer();

  return (
    <S.DrawerLayout $isOpen={isOpen}>
      <S.Overlay onClick={closeDrawer} $isOpen={isOpen} />
      <S.Content $isOpen={isOpen}>
        <Link to="/">
          <Icon icon="Doreburn" width={120} height={15} />
        </Link>
        <S.MenuList>
          {DRAWER_MENU.map((menu, idx) => (
            <S.MenuItem key={idx}>
              <Link to={menu.path}>
                <Icon icon={menu.icon} width={16} height={16} color={theme.colors.gray950} />
                <span>{menu.text}</span>
              </Link>
            </S.MenuItem>
          ))}
        </S.MenuList>
      </S.Content>
    </S.DrawerLayout>
  );
};

export default Drawer;
