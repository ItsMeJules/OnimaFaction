package net.onima.onimafaction.plants;

public interface PlantStage {
	
	int getId();
	int maxId();
	int maxOrdinal();
	int ordinal();
	int minId();
	
	public static PlantStage fromOrdinal(PlantStage stage, int ordinal) {
		return fromId(stage.minId() + ordinal);
	}
	
	public static PlantStage fromId(int id) {
		switch (id) {
		case 0:
			return CropStage.SEEDED;
		case 1:
			return CropStage.GERMINATED;
		case 2:
			return CropStage.VERY_SMALL;
		case 3:
			return CropStage.SMALL;
		case 4:
			return CropStage.MEDIUM;
		case 5:
			return CropStage.TALL;
		case 6:
			return CropStage.VERY_TALL;
		case 7:
			return CropStage.RIPE;
		case 8:
			return BambooStage.SHORT;
		case 9:
			return BambooStage.MEDIUM;
		case 10:
			return BambooStage.TALL;
		case 11:
			return SeedBlockStage.SEEDED;
		case 12:
			return SeedBlockStage.GERMINATED;
		case 13:
			return SeedBlockStage.VERY_SMALL;
		case 14:
			return SeedBlockStage.SMALL;
		case 15:
			return SeedBlockStage.MEDIUM;
		case 16:
			return SeedBlockStage.TALL;
		case 17:
			return SeedBlockStage.VERY_TALL;
		case 18:
			return SeedBlockStage.RIPE;
		case 19:
			return SeedBlockStage.BLOCK_GROWN;
		case 20:
			return NetherWartStage.SEEDED;
		case 21:
			return NetherWartStage.SHORT;
		case 22:
			return NetherWartStage.MEDIUM;
		case 23:
			return NetherWartStage.GROWN;
		default:
			return null;
		}
	}
	
	public enum CropStage implements PlantStage {
		
		SEEDED(0),
		GERMINATED(1),
		VERY_SMALL(2),
		SMALL(3),
		MEDIUM(4),
		TALL(5),
		VERY_TALL(6),
		RIPE(7);
		
		private int id;

		private CropStage(int id) {
			this.id = id;
		}
		
		@Override
		public int getId() {
			return id;
		}
		
		@Override
		public int maxId() {
			return 7;
		}
		
		@Override
		public int maxOrdinal() {
			return 7;
		}
		
		@Override
		public int minId() {
			return 0;
		}
		
		public static CropStage fromId(int id) {
			switch (id) {
			case 0:
				return SEEDED;
			case 1:
				return GERMINATED;
			case 2:
				return VERY_SMALL;
			case 3:
				return SMALL;
			case 4:
				return MEDIUM;
			case 5:
				return TALL;
			case 6:
				return VERY_TALL;
			case 7:
				return RIPE;
			default:
				return null;
			}
		}
		
	}
	
	public enum BambooStage implements PlantStage {
		
		SHORT(8),
		MEDIUM(9),
		TALL(10);
		
		private int id;

		private BambooStage(int id) {
			this.id = id;
		}
		
		@Override
		public int getId() {
			return id;
		}
		
		@Override
		public int maxId() {
			return 10;
		}
		
		@Override
		public int maxOrdinal() {
			return 2;
		}
		
		@Override
		public int minId() {
			return 8;
		}
		
		public static BambooStage fromId(int id) {
			switch (id) {
			case 8:
				return SHORT;
			case 9:
				return MEDIUM;
			case 10:
				return TALL;
			default:
				return null;
			}
		}
		
	}
	
	public enum SeedBlockStage implements PlantStage {
		
		SEEDED(11),
		GERMINATED(12),
		VERY_SMALL(13),
		SMALL(14),
		MEDIUM(15),
		TALL(16),
		VERY_TALL(17),
		RIPE(18),
		BLOCK_GROWN(19);
		
		private int id;

		private SeedBlockStage(int id) {
			this.id = id;
		}
		
		@Override
		public int getId() {
			return id;
		}
		
		@Override
		public int maxId() {
			return 19;
		}
		
		@Override
		public int maxOrdinal() {
			return 8;
		}
		
		@Override
		public int minId() {
			return 11;
		}
		
		public static SeedBlockStage fromOrdinal(int id) {
			switch (id) {
			case 11:
				return SEEDED;
			case 12:
				return GERMINATED;
			case 13:
				return VERY_SMALL;
			case 14:
				return SMALL;
			case 15:
				return MEDIUM;
			case 16:
				return TALL;
			case 17:
				return VERY_TALL;
			case 18:
				return RIPE;
			case 19:
				return BLOCK_GROWN;
			default:
				return null;
			}
		}
		
	}
	
	public enum NetherWartStage implements PlantStage {
		
		SEEDED(20),
		SHORT(21),
		MEDIUM(22),
		GROWN(23);
		
		private int id;

		private NetherWartStage(int id) {
			this.id = id;
		}
		
		@Override
		public int getId() {
			return id;
		}
		
		@Override
		public int maxId() {
			return 23;
		}
		
		@Override
		public int maxOrdinal() {
			return 3;
		}
		
		@Override
		public int minId() {
			return 20;
		}
		
		public static NetherWartStage fromId(int id) {
			switch (id) {
			case 20:
				return SEEDED;
			case 21:
				return SHORT;
			case 22:
				return MEDIUM;
			case 23:
				return GROWN;
			default:
				return null;
			}
		}
		
	}
	
}
