package com.sofar.fun.ad.strategy;

import androidx.annotation.NonNull;

import com.sofar.fun.ad.job.SerialJob;
import com.sofar.fun.ad.task.CountTask;
import com.sofar.fun.ad.task.CountTaskFactory;
import com.sofar.fun.ad.task.ParallelCountTask;
import com.sofar.fun.ad.task.ParallelDelayCountTask;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @param <K> 提交一个K 类型的数据列表
 * @param <V> 返回一个V 类型的数据列表
 */
public class ParallelStrategy<K, V> {

  public Observable<List<V>> applyStrategy(@NonNull List<K> list, int count, int parallelCount,
                                           @NonNull CountTaskFactory<K> taskFactory) {
    return Observable.create((ObservableOnSubscribe<List<V>>) emitter -> {
      List<CountTask> tasks = new ArrayList<>();

      ParallelCountTask parallelCountTask = new ParallelCountTask();
      for (int i = 0; i < list.size(); i++) {
        if (i % parallelCount == 0) {
          parallelCountTask = new ParallelCountTask();
          tasks.add(parallelCountTask);
        }
        parallelCountTask.addTask(taskFactory.createTask(list.get(i)));
      }

      SerialJob job = new SerialJob(tasks, count);
      job.submit(results -> {
        emitter.onNext(results);
        emitter.onComplete();
      });
    }).observeOn(AndroidSchedulers.mainThread());
  }

  /**
   * 将list 按照parallelCount 分组
   */
  public Observable<List<V>> applyDelayStrategy(@NonNull List<K> list, int count, int parallelCount, long delay,
                                                @NonNull CountTaskFactory<K> taskFactory) {
    return Observable.create((ObservableOnSubscribe<List<V>>) emitter -> {
      List<CountTask> tasks = new ArrayList<>();

      ParallelDelayCountTask delayCountTask = new ParallelDelayCountTask();
      for (int i = 0; i < list.size(); i++) {
        if (i % parallelCount == 0) {
          delayCountTask = new ParallelDelayCountTask();
          delayCountTask.setDelay(delay);
          tasks.add(delayCountTask);
        }
        delayCountTask.addTask(taskFactory.createTask(list.get(i)));
      }

      SerialJob job = new SerialJob(tasks, count);
      job.submit(results -> {
        emitter.onNext(results);
        emitter.onComplete();
      });
    }).observeOn(AndroidSchedulers.mainThread());
  }

}
