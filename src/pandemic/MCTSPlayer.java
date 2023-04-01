package pandemic;
 
import java.util.List;
import java.util.function.Function;
import pandemic.Simulation;
public class MCTSPlayer extends AbstractPlayer {

 
  List<GameAction> currentActionList;
  Function<Game, Integer> timeLimit;
  MonteCarloTreeSearch mcts;

  public MCTSPlayer(MonteCarloTreeSearch mcts, Function<Game, Integer> timeLimit) {
    this.mcts = mcts;
    this.timeLimit = timeLimit;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    playPolicy((Game) evt.getNewValue());
  }

  public void playPolicy(Game game) {
    mcts.setLimit(this.timeLimit.apply(game));
    mcts.setRoot(game);
    MCTSNode node = mcts.run();
    game.perform(node.move);
  }
}
