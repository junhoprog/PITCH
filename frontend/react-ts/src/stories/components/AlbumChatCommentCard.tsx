import styled from 'styled-components';
import { colors } from '../../styles/color';
import { useLocation, useNavigate } from 'react-router-dom';
import useStore from '../store/store';
import { useEffect, useState } from 'react';

const ProfileArea = styled.div`
  display: flex;
  width: 100%;
  justify-content: flex-start;
  align-items: center;
  flex-direction: row;
`;
const PostUploadTime = styled.div`
  display: flex;
  font-size: 10px;
  font-family: 'Rg';
  margin: 0px 0px 2px 10px;
  color: ${colors.Font_grey};
`;
const ProfileTextArea = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: flex-end;
`;
const ProfileName = styled.div`
  display: flex;
  font-size: 20px;
  font-family: 'Rg';
  margin-left: 10px;
  color: ${colors.Font_black};
`;
const ProfileImage = styled.div`
  display: flex;
  overflow: hidden;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: ${colors.Main_Pink};
`;

const ChatCardBody = styled.div`
  margin: 10px 0px 10px 0px;
  width: 90%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  line-height: 120%;
`;

const CommentButtonArea = styled.div`
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin: 0 10 0 10px;
`;

const CommentArea = styled.div`
  display: flex;
  width: 100vw;
  height: auto;
  /* overflow: hidden; */
  align-items: center;
  justify-content: flex-start;
  flex-direction: column;
  margin: 0px;
  padding: 10px;
  box-sizing: border-box;
  background-color: ${colors.BG_grey};
  z-index: 10;
`;

const CommentCommentArea = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: auto;
`;

const CommentCommentCard = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  width: 100%;
`;
const CommentCommentIcon = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-end;
  width: 40px;
  height: 100%;
  margin-top: 30px;
`;

const CommentCommentContent = styled.div`
  display: flex;
  width: 100vw;
  height: auto;
  /* overflow: hidden; */
  align-items: center;
  justify-content: flex-start;
  flex-direction: column;
  margin: 0px;
  padding: 10px;
  box-sizing: border-box;
`;
const Line = styled.div`
  width: 95vw;
  height: 1px;
  background-color: ${colors.Button_deactive};
`;
const Text = styled.div<{ fontSize?: string; margin?: string; fontFamily?: string; color?: string }>`
  font-size: ${props => props.fontSize};
  margin: ${props => props.margin};
  font-family: ${props => props.fontFamily};
  color: ${props => props.color};
`;

interface DNA {
  dnaKey: number;
  dnaName: string;
}
interface User {
  id: number;
  username: string;
  profilePicture: string;
  dnas: DNA[];
}

interface AlbumDetail {
  albumId: number;
  title: string;
  albumCover: string;
  artistName: string;
  genre: string;
  spotifyId: string;
}

interface AlbumChatComment {
  albumChatCommentId: number;
  content: string;
  createAt: number; // ISO 날짜 형식
  updateAt: number; // ISO 날짜 형식
  likes: User[];
  comments: AlbumChatComment[]; // 재귀적 구조
  author: User;
}

interface AlbumData {
  comment: AlbumChatComment;
  spotifyAlbumId: string;
}
function AlbumChatCommentCard({ comment, spotifyAlbumId }: AlbumData) {
  ////// Post 시간 계산 //////
  const CreateTime = comment.createAt;
  const UpdatedTime = comment.updateAt;
  const [timeAgo, setTimeAgo] = useState<string>('');
  const server = 'http://203.255.81.70:8030';
  const reissueTokenUrl = `${server}/api/auth/reissued`;
  const [token, setToken] = useState(localStorage.getItem('login-token'));
  const [refreshToken, setRefreshToken] = useState(localStorage.getItem('login-refreshToken'));
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  console.log(timeAgo);
  useEffect(() => {
    const updateTimeAgo = () => {
      if (CreateTime && UpdatedTime) {
        if (UpdatedTime === null) {
          const time = formatTimeAgo(CreateTime);
          console.log('수정안됨');
          setTimeAgo(time);
        } else {
          const time = formatTimeAgo(UpdatedTime);
          console.log('수정됨');
          setTimeAgo(time);
        }
      }
    };

    // 처음 마운트될 때 시간 계산
    updateTimeAgo();
  }, [CreateTime, UpdatedTime]);

  const formatTimeAgo = (unixTimestamp: number): string => {
    const currentTime = Math.floor(Date.now() / 1000); // 현재 시간 (초)
    const timeDifference = currentTime - Math.floor(unixTimestamp); // 경과 시간 (초)

    const minutesAgo = Math.floor(timeDifference / 60); // 경과 시간 (분)
    const hoursAgo = Math.floor(timeDifference / 3600); // 경과 시간 (시간)
    const daysAgo = Math.floor(timeDifference / 86400); // 경과 시간 (일)

    if (minutesAgo < 60) {
      return `${minutesAgo}분 전`;
    } else if (hoursAgo < 24) {
      return `${hoursAgo}시간 전`;
    } else {
      return `${daysAgo}일 전`;
    }
  };
  /////////////

  // 수정/삭제 버튼
  const editMenu = () => {
    console.log('edit');
    setIsDropdownOpen(!isDropdownOpen);
  };

  // chat 좋아요 상태 확인

  // 좋아요 설정
  //   const [isPostLiked, setIsPostLiked] = useState(false);
  //   const [likesCount, setLikesCount] = useState<number>(0);
  //   const { id, name } = useStore();
  //   useEffect(() => {
  //     if (comment) {
  //       setLikesCount(comment.likes.length);
  //       if (comment.likes.some((like: any) => like.id === id)) {
  //         setIsPostLiked(true);
  //       }
  //     }
  //   }, [comment]);

  //   const CommentLikeUrl = server + `/api/album/${spotifyAlbumId}/comment/${comment.albumChatCommentId}/commentLike`;

  //   const changeCommentLike = async () => {
  //     console.log('changing Like');
  //     if (isPostLiked && comment) {
  //       // 이미 좋아요를 눌렀다면 좋아요 취소
  //       setIsPostLiked(false);
  //       setLikesCount(likesCount - 1);
  //       comment.likes = comment.likes.filter((like: any) => like.id !== id);
  //     } else {
  //       // 좋아요 누르기
  //       setIsPostLiked(true);
  //       setLikesCount(likesCount + 1);
  //       comment.likes.push({
  //         id: id,
  //         username: name,
  //         profilePicture: 'string',
  //         dnas: [],
  //       });
  //     }
  //     fetchLike();
  //     // like 데이터 POST 요청
  //   };

  //   const fetchLike = async () => {
  //     if (token) {
  //       console.log('fetching Like Data');
  //       try {
  //         const response = await fetch(CommentLikeUrl, {
  //           method: 'POST',
  //           headers: {
  //             'Content-Type': 'application/json',
  //             Authorization: `Bearer ${token}`,
  //           },
  //         });
  //         if (response.ok) {
  //           const data = await response.json();
  //           if (data.likes.some((like: any) => like.id === id)) {
  //             console.log('like 추가');
  //           } else {
  //             console.log('like 삭제');
  //           }
  //         } else if (response.status === 401) {
  //           await ReissueToken();
  //           fetchLike();
  //         } else {
  //           console.error('Failed to fetch data:', response.status);
  //         }
  //       } catch (error) {
  //         console.error('like 실패:', error);
  //       }
  //     }
  //   };

  const ReissueToken = async () => {
    console.log('reissuing Token');
    try {
      const response = await fetch(reissueTokenUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Refresh-Token': `${refreshToken}`,
        },
      });
      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('login-token', data.token);
        localStorage.setItem('login-refreshToken', data.refreshToken);
        setToken(data.token);
        setRefreshToken(data.refreshToken);
      } else {
        console.error('failed to reissue token', response.status);
      }
    } catch (error) {
      console.error('Refresh Token 재발급 실패', error);
    }
  };

  return (
    <CommentCommentCard>
      <CommentCommentIcon>
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-arrow-return-right" viewBox="0 0 16 16">
          <path
            fill-rule="evenodd"
            d="M1.5 1.5A.5.5 0 0 0 1 2v4.8a2.5 2.5 0 0 0 2.5 2.5h9.793l-3.347 3.346a.5.5 0 0 0 .708.708l4.2-4.2a.5.5 0 0 0 0-.708l-4-4a.5.5 0 0 0-.708.708L13.293 8.3H3.5A1.5 1.5 0 0 1 2 6.8V2a.5.5 0 0 0-.5-.5"
          />
        </svg>
      </CommentCommentIcon>
      <CommentCommentContent>
        <ProfileArea>
          <ProfileImage>
            <img src={comment.author.profilePicture}></img>
          </ProfileImage>
          <ProfileTextArea>
            <ProfileName>{comment.author.username}</ProfileName>
            <PostUploadTime> {timeAgo}</PostUploadTime>
          </ProfileTextArea>
        </ProfileArea>
        <ChatCardBody>
          <Text fontSize="15px">{comment.content}</Text>
        </ChatCardBody>
        {/* <CommentButtonArea>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="14"
            height="14"
            fill={comment.likes.some((like: any) => like.id === id) ? colors.Button_active : colors.Button_deactive}
            className="bi bi-heart-fill"
            viewBox="0 0 16 16"
            onClick={() => changeCommentLike()}
          >
            <path fillRule="evenodd" d="M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314" />
          </svg>
          <Text fontSize="14px" color="grey" margin="0px 20px 0px 5px">
            좋아요 {comment.likes.length}개
          </Text>
          <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="grey" className="bi bi-chat-right-text-fill" viewBox="0 0 16 16" style={{ strokeWidth: 6 }}>
            <path d="M2 1a1 1 0 0 0-1 1v8a1 1 0 0 0 1 1h9.586a2 2 0 0 1 1.414.586l2 2V2a1 1 0 0 0-1-1zm12-1a2 2 0 0 1 2 2v12.793a.5.5 0 0 1-.854.353l-2.853-2.853a1 1 0 0 0-.707-.293H2a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2z" />
            <path d="M3 3.5a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 0 1h-9a.5.5 0 0 1-.5-.5M3 6a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 0 1h-9A.5.5 0 0 1 3 6m0 2.5a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5" />
          </svg>
          <Text fontSize="14px" color="grey" margin="0px 0px 0px 5px">
            답글 {comment.comments.length}개
          </Text>
        </CommentButtonArea> */}
      </CommentCommentContent>
    </CommentCommentCard>
  );
}

export default AlbumChatCommentCard;
