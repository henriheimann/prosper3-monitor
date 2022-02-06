import { AbstractControl, ValidatorFn } from '@angular/forms';

export function MustMatch(controlName: string, matchingControlName: string): ValidatorFn {
  return (control: AbstractControl) => {
    const password = control.get(controlName)?.value;
    const confirmPassword = control.get(matchingControlName)?.value;

    if (password !== confirmPassword) {
      return { MustMatch: true };
    }
    return null;
  };
}
