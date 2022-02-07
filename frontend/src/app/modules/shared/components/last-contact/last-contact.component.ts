import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'p3m-last-contact',
  templateUrl: './last-contact.component.html',
  styleUrls: ['./last-contact.component.sass']
})
export class LastContactComponent {
  date: Date | undefined;

  difference: number | undefined;

  differenceUnit: string | undefined;

  iconColorClass = 'text-success';

  @Input()
  set dateString(dateString: string | null | undefined) {
    if (!dateString) {
      this.date = undefined;
      this.difference = undefined;
      this.differenceUnit = undefined;
      this.iconColorClass = 'text-dark';
      return;
    }

    this.date = new Date(dateString);

    const millisecondDifference = new Date().getTime() - this.date.getTime();
    const secondDifference = Math.floor(millisecondDifference / 1000);
    const minuteDifference = Math.floor(secondDifference / 60);
    const hourDifference = Math.floor(minuteDifference / 60);
    const dayDifference = Math.floor(hourDifference / 24);

    if (minuteDifference <= 10) {
      this.iconColorClass = 'text-success';
    } else if (minuteDifference <= 60) {
      this.iconColorClass = 'text-warning';
    } else {
      this.iconColorClass = 'text-danger';
    }

    if (minuteDifference < 60) {
      this.difference = minuteDifference;
      this.differenceUnit = 'Minuten';
    } else if (hourDifference < 24) {
      this.difference = hourDifference;
      this.differenceUnit = 'Stunden';
    } else {
      this.difference = dayDifference;
      this.differenceUnit = 'Tagen';
    }
  }
}
