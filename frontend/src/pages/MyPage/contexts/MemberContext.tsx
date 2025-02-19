import { createContext } from 'react';

import { MemberContextType } from '../MyPage.types';

export const MemberContext = createContext<MemberContextType | null>(null);
