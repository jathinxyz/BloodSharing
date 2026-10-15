import { BloodGroup } from '@/types';

export const ALL_BLOOD_GROUPS: BloodGroup[] = [
  'O-',
  'O+',
  'A-',
  'A+',
  'B-',
  'B+',
  'AB-',
  'AB+',
];

export const BLOOD_GROUP_INFO: Record<
  BloodGroup,
  { label: string; tag: string; isUniversalDonor?: boolean; isUniversalRecipient?: boolean }
> = {
  'O-': {
    label: 'O Negative',
    tag: 'Universal Red Cell Donor',
    isUniversalDonor: true,
  },
  'O+': {
    label: 'O Positive',
    tag: 'Most common blood group',
  },
  'A-': {
    label: 'A Negative',
    tag: 'Rare blood group',
  },
  'A+': {
    label: 'A Positive',
    tag: 'Common blood group',
  },
  'B-': {
    label: 'B Negative',
    tag: 'Rare blood group',
  },
  'B+': {
    label: 'B Positive',
    tag: 'High demand blood group',
  },
  'AB-': {
    label: 'AB Negative',
    tag: 'Rarest blood group',
  },
  'AB+': {
    label: 'AB Positive',
    tag: 'Universal Red Cell Recipient',
    isUniversalRecipient: true,
  },
};
