import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-timer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './timer.component.html',
  styleUrls: ['./timer.component.scss']
})
export class TimerComponent implements OnInit, OnDestroy {
  @Input() dataFechamento!: string;
  tempoRestante: string = '';
  timerClass: string = '';
  private subscription!: Subscription;

  ngOnInit(): void {
    if (!this.dataFechamento) {
      console.error('dataFechamento é nulo ou vazio:', this.dataFechamento);
      this.tempoRestante = 'Data inválida';
      this.timerClass = 'expired';
      return;
    }

    console.log('dataFechamento recebido:', this.dataFechamento);
    this.updateTimer();
    this.subscription = interval(1000).subscribe(() => this.updateTimer());
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  private updateTimer(): void {
    const now = new Date();
    const fechamento = new Date(this.dataFechamento);

    if (isNaN(fechamento.getTime())) {
      console.error('dataFechamento inválida:', this.dataFechamento);
      this.tempoRestante = 'Data inválida';
      this.timerClass = 'expired';
      return;
    }

    const diffMs = fechamento.getTime() - now.getTime();

    if (diffMs <= 0) {
      this.tempoRestante = 'Sessão encerrada';
      this.timerClass = 'expired';
      this.subscription.unsubscribe(); // Para o intervalo
      return;
    }

    const minutes = Math.floor(diffMs / 1000 / 60);
    const seconds = Math.floor((diffMs / 1000) % 60);
    this.tempoRestante = `${minutes}m ${seconds.toString().padStart(2, '0')}s`;

    if (minutes > 10) {
      this.timerClass = 'green';
    } else if (minutes >= 5) {
      this.timerClass = 'yellow';
    } else {
      this.timerClass = 'red';
    }
  }
}
