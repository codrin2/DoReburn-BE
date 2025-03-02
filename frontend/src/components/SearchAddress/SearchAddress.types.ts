export interface AddressMainProps {
  title: string;
  coordinateX: number;
  coordinateY: number;
  address?: string;
}

export interface AddressOnboardingProps extends AddressMainProps {
  address: string;
}
