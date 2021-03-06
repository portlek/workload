package io.github.portlek.workload;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

final class WorkloadTest {

  private final TypedDistributedTask<Player> playerTask = TypedDistributedTask.<Player>builder()
    .distributionSize(20)
    .action(player -> player.sendMessage("Your name is " + player.getName()))
    .escapeCondition(Objects::isNull)
    .build();

  public static void main(final String[] args) throws InterruptedException {
    final var test = new WorkloadTest();
    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(test::exec, 0L, 5L, TimeUnit.MICROSECONDS);
    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(test.playerTask, 50L, 50L, TimeUnit.MICROSECONDS);
    while (true) {
      Thread.sleep(5L);
    }
  }

  public void exec() {
    this.join(new Player() {
      @NotNull
      @Override
      public String getName() {
        return UUID.randomUUID().toString();
      }

      @Override
      public void sendMessage(@NotNull final String message) {
        System.out.println(message);
      }
    });
  }

  public void join(@NotNull final Player player) {
    this.playerTask.add(() -> player);
  }
}
